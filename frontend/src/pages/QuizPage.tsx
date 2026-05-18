import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CheckCircle, XCircle, ArrowLeft, ArrowRight, RotateCcw, Home, BookOpen, Trophy } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { StageService } from '../services/StageService';
import { ProgressService } from '../services/ProgressService';
import {
  OfflineProgressQueue,
  subscribeProgressSyncStatus,
  type ProgressSyncStatus,
} from '../services/OfflineProgressQueue';
import { QuizSessionService } from '../services/QuizSessionService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { Stage, Word, QuizMode, IncorrectWord } from '../types';

type QuizPhase = 'loading' | 'error' | 'incorrect-note' | 'mode-select' | 'quiz' | 'result';

interface QuestionResult {
  word: Word;
  userAnswer: string;
  correctAnswer: string;
  isCorrect: boolean;
}

const DIFFICULTY_LABEL: Record<string, string> = {
  easy: '하',
  medium: '중',
  hard: '상',
};

const DIFFICULTY_FULL: Record<string, string> = {
  easy: '하 (쉬움)',
  medium: '중 (보통)',
  hard: '상 (어려움)',
};

const DIFFICULTY_BADGE: Record<string, string> = {
  easy: 'bg-green-100 text-green-700',
  medium: 'bg-amber-100 text-amber-700',
  hard: 'bg-red-100 text-red-700',
};

export default function QuizPage() {
  const { stageId } = useParams<{ stageId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [phase, setPhase] = useState<QuizPhase>('loading');
  const [stage, setStage] = useState<Stage | null>(null);
  const [incorrectWords, setIncorrectWords] = useState<IncorrectWord[]>([]);
  const [wordsToResolve, setWordsToResolve] = useState<Word[]>([]);
  const [showResolveDialog, setShowResolveDialog] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const [mode, setMode] = useState<QuizMode>('KtoE');
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answer, setAnswer] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [results, setResults] = useState<QuestionResult[]>([]);
  const [syncStatus, setSyncStatus] = useState<ProgressSyncStatus>(
    OfflineProgressQueue.hasPending() ? 'queued' : 'idle',
  );

  const inputRef = useRef<HTMLInputElement>(null);
  const nextBtnRef = useRef<HTMLButtonElement>(null);
  const feedbackRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!stageId) {
      setErrorMsg('스테이지 정보가 없습니다.');
      setPhase('error');
      return;
    }

    const sId = Number(stageId);

    Promise.all([
      StageService.getStageById(sId),
      ProgressService.getIncorrectWords()
    ])
      .then(([s, allIw]) => {
        setStage(s);
        // Filter incorrect words for this stage
        const stageIw = allIw.filter(iw => s.words.some(w => w.wordId === iw.wordId));
        setIncorrectWords(stageIw);

        if (stageIw.length > 0) {
          setPhase('incorrect-note');
        } else {
          setPhase('mode-select');
        }
      })
      .catch(() => {
        setErrorMsg('데이터를 불러오지 못했습니다.');
        setPhase('error');
      });
  }, [stageId]);

  useEffect(() => {
    if (phase === 'result') window.scrollTo({ top: 0, behavior: 'instant' });
  }, [phase]);

  useEffect(() => subscribeProgressSyncStatus(setSyncStatus), []);

  useEffect(() => {
    void ProgressService.syncQueuedProgress();
  }, []);

  useEffect(() => {
    if (phase === 'quiz' && !submitted) {
      inputRef.current?.focus();
    } else if (phase === 'quiz' && submitted) {
      // blur input first so mobile keyboard dismisses and full viewport is restored
      inputRef.current?.blur();
      feedbackRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      // Delay focus so the Enter keydown event from the input doesn't leak into the button
      const id = setTimeout(() => nextBtnRef.current?.focus({ preventScroll: true }), 150);
      return () => clearTimeout(id);
    }
  }, [phase, currentIndex, submitted]);

  const words = (stage?.words ?? []).slice().sort((a, b) => a.orderIndex - b.orderIndex);

  const getPrompt = (word: Word) => (mode === 'KtoE' ? word.meaning : word.word);
  const getCorrect = (word: Word) => (mode === 'KtoE' ? word.word : word.meaning);

  const handleStartQuiz = (selectedMode: QuizMode) => {
    setMode(selectedMode);
    setCurrentIndex(0);
    setResults([]);
    setAnswer('');
    setSubmitted(false);
    setPhase('quiz');
  };

  const handleSubmit = () => {
    const word = words[currentIndex];
    if (!word || submitted) return;
    const correctAnswer = getCorrect(word);
    const isCorrect = answer.trim().toLowerCase() === correctAnswer.trim().toLowerCase();
    setResults((prev) => [...prev, { word, userAnswer: answer, correctAnswer, isCorrect }]);
    setSubmitted(true);
  };

  const handleSkip = () => {
    const word = words[currentIndex];
    if (!word || submitted) return;
    setResults((prev) => [...prev, { word, userAnswer: '', correctAnswer: getCorrect(word), isCorrect: false }]);
    setSubmitted(true);
  };

  const handleNext = () => {
    if (currentIndex < words.length - 1) {
      setCurrentIndex((i) => i + 1);
      setAnswer('');
      setSubmitted(false);
    } else {
      const finalResults = [...results];
      saveProgress(finalResults);
      setPhase('result');
    }
  };

  const saveProgress = async (finalResults: QuestionResult[]) => {
    if (!user || !stageId) return;
    const sId = Number(stageId);

    // [1] Update local state immediately with newly failed words (for Mock environments)
    const newlyFailed = finalResults.filter((r) => !r.isCorrect);
    if (newlyFailed.length > 0) {
      setIncorrectWords((prev) => {
        const next = [...prev];
        newlyFailed.forEach((nf) => {
          if (!next.some((iw) => iw.wordId === nf.word.wordId)) {
            next.push({
              incorrectWordId: Date.now() + Math.random(), // Temporary ID for mock
              wordId: nf.word.wordId,
              uid: user.uid,
              count: 1,
              lastIncorrectAt: new Date().toISOString(),
            });
          }
        });
        return next;
      });
    }

    // [2] Identify words to resolve (was incorrect, now correct) - use current state before sync
    const correctIds = new Set(finalResults.filter((r) => r.isCorrect).map((r) => r.word.wordId));
    const toResolve =
      stage?.words.filter(
        (w) => correctIds.has(w.wordId) && incorrectWords.some((iw) => iw.wordId === w.wordId),
      ) ?? [];

    // [3] Save and Sync (Async)
    try {
      await QuizSessionService.saveQuizResult({
        stageId: sId,
        score: finalResults.filter((r) => r.isCorrect).length,
        totalQuestions: finalResults.length,
        answers: finalResults.map((r) => ({
          wordId: r.word.wordId,
          userAnswer: r.userAnswer,
          correct: r.isCorrect,
        })),
      });

      await ProgressService.markStageCompleted(sId);

      // Re-fetch incorrect words to reflect new failures immediately from DB if possible
      const allIw = await ProgressService.getIncorrectWords();
      if (stage) {
        const stageIw = allIw.filter((iw) => stage.words.some((w) => w.wordId === iw.wordId));
        setIncorrectWords(stageIw);
      }
    } catch (err) {
      console.error('Failed to sync progress:', err);
      try {
        await ProgressService.markStageCompleted(sId);
      } catch {
        // ProgressService has already queued the completion when a retry is possible.
      }
      setSyncStatus(OfflineProgressQueue.hasPending() ? 'queued' : 'failed');
    }

    // [4] Show Resolve Dialog if there are words to resolve
    if (toResolve.length > 0) {
      setWordsToResolve(toResolve);
      setShowResolveDialog(true);
    }
  };

  const handleResolve = async () => {
    try {
      await Promise.all(wordsToResolve.map((w) => ProgressService.resolveIncorrectWord(w.wordId)));
    } catch (err) {
      console.error('Failed to resolve words:', err);
      // If in DEV mode, proceed to update local state anyway so the mock UI works
      if (!import.meta.env.DEV) return;
    }

    // Sync local state after resolution (or mock resolution)
    setIncorrectWords((prev) =>
      prev.filter((iw) => !wordsToResolve.some((w) => w.wordId === iw.wordId)),
    );
    setShowResolveDialog(false);
  };

  const handleRetry = () => {
    setCurrentIndex(0);
    setResults([]);
    setAnswer('');
    setSubmitted(false);
    if (incorrectWords.length > 0) {
      setPhase('incorrect-note');
    } else {
      setPhase('mode-select');
    }
  };

  const handleExitQuiz = () => {
    setCurrentIndex(0);
    setResults([]);
    setAnswer('');
    setSubmitted(false);
    setPhase('mode-select');
  };

  // ── Loading ───────────────────────────────────────────────

  if (phase === 'loading') {
    return (
      <div
        role="status"
        aria-label="퀴즈 데이터를 불러오는 중입니다"
        className="min-h-screen bg-slate-50 flex items-center justify-center"
      >
        <LoadingSpinner />
      </div>
    );
  }

  // ── Error ─────────────────────────────────────────────────

  if (phase === 'error') {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div
          role="alert"
          className="bg-white rounded-2xl shadow-sm border border-slate-100 p-8 max-w-sm w-full text-center"
        >
          <p className="text-slate-900 font-semibold mb-2">오류가 발생했습니다</p>
          <p className="text-slate-500 text-sm mb-6">{errorMsg}</p>
          <button
            type="button"
            onClick={() => navigate(-1)}
            title="이전 페이지로 돌아갑니다"
            className="w-full py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold transition-colors"
          >
            돌아가기
          </button>
        </div>
      </div>
    );
  }

  // ── Incorrect Note ───────────────────────────────────────

  if (phase === 'incorrect-note' && stage) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-8 max-w-sm w-full">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-amber-50 rounded-lg">
              <BookOpen className="w-6 h-6 text-amber-600" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-slate-900">Incorrect Note</h2>
              <p className="text-sm text-slate-500">지난번에 틀린 단어들입니다</p>
            </div>
          </div>

          <div className="max-h-60 overflow-y-auto pr-1 flex flex-col gap-2 mb-8">
            {incorrectWords.map((iw) => {
              const word = stage.words.find((w) => w.wordId === iw.wordId);
              if (!word) return null;
              return (
                <div
                  key={iw.incorrectWordId}
                  className="p-3 bg-amber-50 rounded-xl flex justify-between items-center animate-in fade-in slide-in-from-bottom-2"
                >
                  <span className="font-bold text-slate-900">{word.word}</span>
                  <span className="text-sm text-slate-500">{word.meaning}</span>
                </div>
              );
            })}
          </div>

          <button
            type="button"
            onClick={() => setPhase('mode-select')}
            className="w-full py-4 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-bold transition-all flex items-center justify-center gap-2"
          >
            모드 선택으로 계속하기
            <ArrowRight className="w-5 h-5" />
          </button>
        </div>
      </div>
    );
  }

  // ── Mode Select ───────────────────────────────────────────

  if (phase === 'mode-select' && stage) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-8 max-w-sm w-full">
          <button
            type="button"
            onClick={() => navigate(-1)}
            title="이전 페이지로 돌아갑니다"
            aria-label="이전 페이지로 돌아가기"
            className="flex items-center gap-1 text-slate-400 hover:text-slate-700 text-sm mb-4 -ml-1 transition-colors"
          >
            <ArrowLeft aria-hidden="true" className="w-4 h-4" />
            이전
          </button>
          <div className="flex items-center gap-2 mb-6">
            <span
              aria-label={`난이도: ${DIFFICULTY_FULL[stage.difficulty]}`}
              className={`text-xs font-semibold px-2.5 py-0.5 rounded-full ${DIFFICULTY_BADGE[stage.difficulty]}`}
            >
              {DIFFICULTY_LABEL[stage.difficulty]}
            </span>
            <span className="text-slate-500 text-sm">Stage {stage.stageNumber}</span>
          </div>

          <h2 className="text-xl font-bold text-slate-900 mb-1">퀴즈 모드 선택</h2>
          <p className="text-slate-500 text-sm mb-8">원하는 방향으로 풀어보세요</p>

          <div role="group" aria-label="퀴즈 모드" className="flex flex-col gap-3">
            <button
              type="button"
              onClick={() => handleStartQuiz('KtoE')}
              title="한국어 뜻을 보고 영단어를 직접 입력하는 모드"
              aria-label="한→영 모드: 한국어 뜻을 보고 영단어를 입력합니다"
              className="w-full py-4 px-5 rounded-xl border-2 border-slate-200 hover:border-indigo-500 hover:bg-indigo-50 text-left transition-all group"
            >
              <span className="block font-semibold text-slate-900 group-hover:text-indigo-700">
                한 ➡️ EN
              </span>
              <span className="block text-sm text-slate-500 mt-0.5">
                한국어 뜻을 보고 영단어를 입력
              </span>
            </button>
            <button
              type="button"
              onClick={() => handleStartQuiz('EtoK')}
              title="영단어를 보고 한국어 뜻을 직접 입력하는 모드"
              aria-label="영→한 모드: 영단어를 보고 한국어 뜻을 입력합니다"
              className="w-full py-4 px-5 rounded-xl border-2 border-slate-200 hover:border-indigo-500 hover:bg-indigo-50 text-left transition-all group"
            >
              <span className="block font-semibold text-slate-900 group-hover:text-indigo-700">
                EN ➡️ 한
              </span>
              <span className="block text-sm text-slate-500 mt-0.5">
                영단어를 보고 한국어 뜻을 입력
              </span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  // ── Quiz ──────────────────────────────────────────────────

  const currentWord = words[currentIndex];
  const lastResult = results[results.length - 1] ?? null;
  const isLastQuestion = currentIndex === words.length - 1;
  const progressValue = currentIndex + (submitted ? 1 : 0);
  const inputLabel = mode === 'KtoE' ? '영단어 입력' : '한국어 뜻 입력';

  if (phase === 'quiz' && currentWord && stage) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 w-full max-w-sm flex flex-col">
          {/* Header */}
          <div className="flex items-center justify-between mb-3">
            <button
              type="button"
              onClick={handleExitQuiz}
              title="퀴즈를 중단하고 모드 선택으로 돌아갑니다"
              aria-label="퀴즈 중단, 모드 선택으로 돌아가기"
              className="flex items-center gap-1 text-slate-400 hover:text-slate-600 text-sm transition-colors"
            >
              <ArrowLeft aria-hidden="true" className="w-4 h-4" />
              이전
            </button>
            <span className="text-sm text-slate-400" aria-label={`${words.length}문제 중 ${currentIndex + 1}번째`}>
              {currentIndex + 1} / {words.length}
            </span>
            <span
              aria-label={`퀴즈 모드: ${mode === 'KtoE' ? '한국어에서 영어' : '영어에서 한국어'}`}
              className={`text-xs font-semibold px-2.5 py-0.5 rounded-full ${DIFFICULTY_BADGE[stage.difficulty]}`}
            >
              {mode === 'KtoE' ? '한➡️EN' : 'EN➡️한'}
            </span>
          </div>

          {/* Progress bar — NFR-01: 300ms transition well under 0.5s */}
          <div
            role="progressbar"
            aria-valuenow={progressValue}
            aria-valuemin={0}
            aria-valuemax={words.length}
            aria-label={`퀴즈 진행률: ${progressValue}/${words.length}`}
            className="h-1.5 bg-slate-100 rounded-full mb-6 overflow-hidden"
          >
            <div
              className="h-full bg-indigo-600 rounded-full transition-all duration-300"
              style={{ width: `${(progressValue / words.length) * 100}%` }}
            />
          </div>

          {/* Prompt */}
          <div className="mb-6">
            <p className="text-xs text-slate-400 uppercase tracking-wide mb-2">
              {mode === 'KtoE' ? '뜻' : '단어'}
            </p>
            <p id="quiz-prompt" className="text-2xl font-bold text-slate-900 leading-snug break-keep">
              {getPrompt(currentWord)}
            </p>
            {currentWord.example && !submitted && (
              <p className="text-sm text-slate-400 mt-2 italic" aria-label={`예문: ${currentWord.example}`}>
                예: {currentWord.example}
              </p>
            )}
          </div>

          {/* Input (pre-submit) */}
          {!submitted && (
            <div>
              <label htmlFor="quiz-answer" className="sr-only">
                {inputLabel}
              </label>
              <input
                id="quiz-answer"
                ref={inputRef}
                type="text"
                value={answer}
                onChange={(e) => setAnswer(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && answer.trim()) handleSubmit();
                }}
                placeholder={mode === 'KtoE' ? '영단어를 입력하세요' : '뜻을 입력하세요'}
                aria-label={inputLabel}
                aria-describedby="quiz-prompt"
                className="w-full border border-slate-200 rounded-xl px-4 py-3 text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent mb-4 transition-all"
              />
              <button
                type="button"
                onClick={handleSkip}
                title="이 문제를 모르면 오답으로 처리하고 넘어갑니다"
                aria-label="문제 스킵 (오답 처리)"
                className="w-full mb-2 py-1.5 text-xs text-gray-400 hover:text-gray-600 transition-colors"
              >
                모르겠어요 (오답 처리)
              </button>
              <button
                type="button"
                onClick={handleSubmit}
                disabled={!answer.trim()}
                title="입력한 답을 제출합니다 (Enter 키로도 제출 가능)"
                aria-label="답 제출"
                className="w-full py-3 rounded-xl bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold transition-colors"
              >
                제출
              </button>
            </div>
          )}

          {/* Feedback (post-submit) — role="alert" announces to screen readers immediately */}
          {submitted && lastResult && (
            <div>
              <div
                ref={feedbackRef}
                role="alert"
                aria-live="assertive"
                className={`rounded-xl p-4 mb-4 flex items-start gap-3 ${
                  lastResult.isCorrect ? 'bg-green-50' : 'bg-red-50'
                }`}
              >
                {lastResult.isCorrect ? (
                  <CheckCircle aria-hidden="true" className="text-green-600 w-5 h-5 mt-0.5 shrink-0" />
                ) : (
                  <XCircle aria-hidden="true" className="text-red-500 w-5 h-5 mt-0.5 shrink-0" />
                )}
                <div>
                  <p
                    className={`font-semibold text-sm ${
                      lastResult.isCorrect ? 'text-green-700' : 'text-red-600'
                    }`}
                  >
                    {lastResult.isCorrect ? '정답!' : '오답'}
                  </p>
                  {!lastResult.isCorrect && (
                    <>
                      <p className="text-sm text-slate-500 mt-1">
                        내 답:{' '}
                        <span className="text-slate-700">{lastResult.userAnswer || '(없음)'}</span>
                      </p>
                      <p className="text-sm text-slate-500">
                        정답:{' '}
                        <span className="font-medium text-slate-900">{lastResult.correctAnswer}</span>
                      </p>
                    </>
                  )}
                </div>
              </div>

              <button
                ref={nextBtnRef}
                type="button"
                onClick={handleNext}
                title={isLastQuestion ? '모든 문제를 완료하고 결과를 확인합니다' : '다음 문제로 넘어갑니다'}
                aria-label={isLastQuestion ? '결과 페이지로 이동' : `${currentIndex + 2}번 문제로 이동`}
                className="w-full py-3 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold transition-colors flex items-center justify-center gap-2"
              >
                {isLastQuestion ? '결과 보기' : '다음 문제'}
                <ArrowRight aria-hidden="true" className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      </div>
    );
  }

  // ── Result ────────────────────────────────────────────────

  const score = results.filter((r) => r.isCorrect).length;
  const total = results.length;

  const scoreLabel =
    total === 0
      ? '—'
      : score === total
      ? '완벽해요! 🎉'
      : score >= Math.ceil(total * 0.7)
      ? '잘했어요!'
      : '더 연습해봐요';

  const scoreLabelColor =
    total === 0
      ? 'text-slate-400'
      : score === total
      ? 'text-green-600'
      : score >= Math.ceil(total * 0.7)
      ? 'text-amber-600'
      : 'text-red-500';

  const syncLabel: Record<ProgressSyncStatus, string> = {
    idle: '저장 대기',
    queued: '오프라인 저장됨',
    syncing: '동기화 중',
    synced: '저장 완료',
    failed: '동기화 실패',
  };

  const syncClass: Record<ProgressSyncStatus, string> = {
    idle: 'bg-slate-100 text-slate-500',
    queued: 'bg-amber-100 text-amber-700',
    syncing: 'bg-indigo-100 text-indigo-700',
    synced: 'bg-green-100 text-green-700',
    failed: 'bg-red-100 text-red-600',
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-in fade-in duration-300">
      <div className="bg-white w-full max-w-lg rounded-[3rem] shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300 flex flex-col max-h-[90vh]">
        {/* Header with Trophy */}
        <div className="bg-white p-4 pt-8 text-center relative border-b border-slate-50">
          <div className="flex justify-center mb-1">
            <div className="bg-amber-50 p-2 rounded-xl animate-bounce-subtle">
              <Trophy className="w-6 h-6 text-amber-600" />
            </div>
          </div>
          <h2 className={`text-lg font-black mb-0.5 ${scoreLabelColor}`}>{scoreLabel}</h2>
          <div className="mb-2 flex justify-center">
            <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${syncClass[syncStatus]}`}>
              {syncLabel[syncStatus]}
            </span>
          </div>
          <div className="flex justify-center items-baseline gap-1.5">
            <span className="text-3xl font-black text-slate-900">{score}</span>
            <span className="text-slate-300 text-lg font-black">/</span>
            <span className="text-lg font-black text-slate-400">{total}</span>
          </div>
        </div>

        {/* Scrollable Result List */}
        <div className="flex-1 overflow-y-auto p-8 pt-12">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4">상세 결과</h3>
          <div className="flex flex-col gap-3">
            {results.map((r, i) => {
              const prompt = mode === 'KtoE' ? r.word.meaning : r.word.word;
              return (
                <div
                  key={i}
                  className={`p-4 rounded-2xl border flex items-center gap-4 bg-white ${
                    r.isCorrect ? 'border-green-100' : 'border-slate-100'
                  }`}
                >
                  <div
                    className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${
                      r.isCorrect ? 'bg-green-500 text-white' : 'bg-red-500 text-white'
                    }`}
                  >
                    {r.isCorrect ? (
                      <CheckCircle className="w-5 h-5" />
                    ) : (
                      <XCircle className="w-5 h-5" />
                    )}
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex justify-between items-start mb-0.5">
                      <p className="font-bold text-slate-900 truncate">{prompt}</p>
                      <p className="text-[10px] font-black text-slate-300 uppercase tracking-widest">
                        Q.{i + 1}
                      </p>
                    </div>
                    <div className="flex items-center gap-x-3 gap-y-1">
                      {r.isCorrect ? (
                        <p className="text-sm text-slate-500 font-medium">{r.correctAnswer}</p>
                      ) : (
                        <div className="flex items-center gap-2">
                          <span className="text-xs line-through text-slate-300 decoration-red-500 decoration-2">
                            {r.userAnswer || '(공백)'}
                          </span>
                          <ArrowRight className="w-3 h-3 text-slate-300" />
                          <span className="text-xs font-bold text-red-600">{r.correctAnswer}</span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Footer Buttons */}
        <div className="p-8 bg-slate-50 border-t border-slate-100 flex gap-3">
          <button
            type="button"
            onClick={() => navigate('/select')}
            className="flex-1 py-4 rounded-2xl bg-white border border-slate-200 text-slate-700 font-bold hover:bg-slate-100 transition-all flex items-center justify-center gap-2"
          >
            <Home className="w-5 h-5" />
            홈으로
          </button>
          <button
            type="button"
            onClick={handleRetry}
            className="flex-1 py-4 rounded-2xl bg-indigo-600 text-white font-bold hover:bg-indigo-700 shadow-lg shadow-indigo-200 transition-all flex items-center justify-center gap-2"
          >
            <RotateCcw className="w-5 h-5" />
            다시 풀기
          </button>
        </div>
      </div>

      {/* Resolve Dialog Overlay */}
      {showResolveDialog && (
        <div className="absolute inset-0 z-[60] flex items-center justify-center p-6 bg-white/20 backdrop-blur-md animate-in fade-in duration-300">
          <div className="bg-white w-full max-w-sm rounded-[2.5rem] shadow-2xl p-8 border border-slate-100 animate-in zoom-in-90 duration-300">
            <div className="flex flex-col items-center text-center">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
                <CheckCircle className="w-8 h-8 text-green-600" />
              </div>
              <h3 className="text-xl font-bold text-slate-900 mb-2">Great Progress!</h3>
              <p className="text-slate-500 text-sm mb-6">
                이전에 틀렸던 {wordsToResolve.length}개의 단어를 맞췄어요!<br />
                오답노트에서 제거할까요?
              </p>

              <div className="w-full flex flex-col gap-3">
                <button
                  type="button"
                  onClick={handleResolve}
                  className="w-full py-4 rounded-2xl bg-indigo-600 text-white font-bold hover:bg-indigo-700 transition-all"
                >
                  제거하기
                </button>
                <button
                  type="button"
                  onClick={() => setShowResolveDialog(false)}
                  className="w-full py-4 rounded-2xl bg-white text-slate-400 font-bold hover:text-slate-600 transition-all"
                >
                  나중에 하기
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
