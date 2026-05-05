import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CheckCircle, XCircle, ArrowLeft, ArrowRight, RotateCcw, Home } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { StageService } from '../services/StageService';
import { ProgressService } from '../services/ProgressService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { Stage, Word, QuizMode } from '../types';

type QuizPhase = 'loading' | 'error' | 'mode-select' | 'quiz' | 'result';

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
  const [errorMsg, setErrorMsg] = useState('');

  const [mode, setMode] = useState<QuizMode>('KtoE');
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answer, setAnswer] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [results, setResults] = useState<QuestionResult[]>([]);

  const inputRef = useRef<HTMLInputElement>(null);
  const nextBtnRef = useRef<HTMLButtonElement>(null);
  const feedbackRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!stageId) {
      setErrorMsg('스테이지 정보가 없습니다.');
      setPhase('error');
      return;
    }
    StageService.getStageById(stageId)
      .then((s) => {
        setStage(s);
        setPhase('mode-select');
      })
      .catch(() => {
        setErrorMsg('스테이지를 불러오지 못했습니다.');
        setPhase('error');
      });
  }, [stageId]);

  useEffect(() => {
    if (phase === 'result') window.scrollTo({ top: 0, behavior: 'instant' });
  }, [phase]);

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
      saveProgress(results);
      setPhase('result');
    }
  };

  const saveProgress = (finalResults: QuestionResult[]) => {
    if (!user || !stageId || !stage) return;
    const score = finalResults.filter((r) => r.isCorrect).length;
    ProgressService.saveProgress({
      progressId: '',
      userId: user.uid,
      stageId,
      completed: true,
      lastScore: score,
      incorrectWords: finalResults
        .filter((r) => !r.isCorrect)
        .map((r) => ({
          incorrectWordId: '',
          progressId: '',
          wordId: r.word.wordId,
          isResolved: false,
        })),
      updatedAt: new Date().toISOString(),
    }).catch(() => {});
  };

  const handleRetry = () => {
    setCurrentIndex(0);
    setResults([]);
    setAnswer('');
    setSubmitted(false);
    setPhase('mode-select');
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
    score === total
      ? '완벽해요! 🎉'
      : score >= Math.ceil(total * 0.7)
      ? '잘했어요!'
      : '더 연습해봐요';

  const scoreLabelColor =
    score === total
      ? 'text-green-600'
      : score >= Math.ceil(total * 0.7)
      ? 'text-amber-600'
      : 'text-red-500';

  return (
    <div className="min-h-screen bg-slate-50 py-8 px-4">
      <div className="max-w-5xl mx-auto">

        {/* Score summary
            Mobile : 텍스트 중앙, 버튼 전체 너비
            PC     : 점수(좌) + 버튼(우) 가로 배치 */}
        <div
          role="region"
          aria-label="퀴즈 결과 요약"
          className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 mb-6"
        >
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div className="text-center md:text-left">
              <p className="text-slate-500 text-sm mb-1">최종 점수</p>
              <p
                className="text-4xl font-bold text-slate-900 mb-1"
                aria-label={`${total}문제 중 ${score}문제 정답`}
              >
                {score} <span aria-hidden="true" className="text-slate-300">/</span> {total}
              </p>
              <p className={`text-sm font-medium ${scoreLabelColor}`}>{scoreLabel}</p>
            </div>
            <div className="flex gap-2 md:flex-shrink-0">
              <button
                type="button"
                onClick={() => navigate('/')}
                title="홈 화면으로 이동합니다"
                aria-label="홈으로 이동"
                className="flex-1 md:flex-none md:px-6 py-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 font-medium transition-colors flex items-center justify-center gap-1.5 text-sm"
              >
                <Home aria-hidden="true" className="w-4 h-4" />홈
              </button>
              <button
                type="button"
                onClick={handleRetry}
                title="모드 선택부터 다시 시작합니다"
                aria-label="퀴즈 다시 풀기"
                className="flex-1 md:flex-none md:px-6 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold transition-colors flex items-center justify-center gap-1.5 text-sm"
              >
                <RotateCcw aria-hidden="true" className="w-4 h-4" />다시 풀기
              </button>
            </div>
          </div>
        </div>

        {/* Per-question breakdown
            Mobile        : 1열 가로 리스트 (grid-cols-1)
            md  768px+    : 3열 정사각형 카드 → 3×3 + 1
            lg  1024px+   : 4열 정사각형 카드 → 4×2 + 2
            xl  1280px+   : 5열 정사각형 카드 → 5×2      */}
        <ol
          aria-label="문항별 결과"
          className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3"
        >
          {results.map((r, i) => (
            <li
              key={r.word.wordId}
              aria-label={`${i + 1}번 문제: ${r.word.word} — ${r.isCorrect ? '정답' : '오답'}`}
              className={`bg-white rounded-xl shadow-sm border border-slate-100
                px-4 py-3 flex items-start gap-3
                md:aspect-square md:relative md:flex-col md:items-center md:justify-center md:text-center md:p-4 md:gap-2
                ${r.isCorrect ? '' : 'md:border-red-100'}`}
            >
              {/* 번호: 모바일 인라인 / PC 좌상단 절대 위치 */}
              <span
                aria-hidden="true"
                className="text-xs text-slate-400 w-5 text-center shrink-0 mt-0.5
                           md:absolute md:top-2.5 md:left-3 md:w-auto md:mt-0"
              >
                {i + 1}
              </span>

              {/* O/X 아이콘: 모바일 소형 / PC 대형 */}
              {r.isCorrect ? (
                <CheckCircle
                  aria-hidden="true"
                  className="text-green-500 shrink-0 mt-0.5 w-5 h-5 md:w-10 md:h-10 md:mt-0"
                />
              ) : (
                <XCircle
                  aria-hidden="true"
                  className="text-red-400 shrink-0 mt-0.5 w-5 h-5 md:w-10 md:h-10 md:mt-0"
                />
              )}

              {/* 단어 정보 */}
              <div className="min-w-0 md:w-full">
                <p className="font-medium text-slate-900 text-sm md:text-base md:font-bold">
                  {r.word.word}
                </p>
                <p className="text-xs text-slate-500 truncate md:text-sm md:whitespace-normal md:line-clamp-2">
                  {r.word.meaning}
                </p>
                {!r.isCorrect && (
                  <p className="text-xs text-red-400 mt-0.5 truncate md:whitespace-normal md:line-clamp-1">
                    내 답: {r.userAnswer || '(없음)'}
                  </p>
                )}
              </div>

              <span className="sr-only">
                {r.isCorrect ? '정답' : `오답, 정답은 ${r.correctAnswer}`}
              </span>
            </li>
          ))}
        </ol>

      </div>
    </div>
  );
}
