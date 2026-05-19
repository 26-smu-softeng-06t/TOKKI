import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { AnimatePresence, motion } from 'framer-motion';
import { ChevronLeft, AlertCircle, Volume2 } from 'lucide-react';
import { toast } from 'sonner';
import { StageService } from '../services/StageService';
import { WordRelationService } from '../services/WordRelationService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { Word, WordRelation } from '../types';

export default function LearnPage() {
  const { stageId } = useParams<{ stageId: string }>();
  const navigate = useNavigate();

  const [words, setWords] = useState<Word[]>([]);
  const [stageInfo, setStageInfo] = useState<{ difficulty: string; stageNumber: number } | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [relations, setRelations] = useState<WordRelation[]>([]);
  const [loadingRelations, setLoadingRelations] = useState(false);
  const [speaking, setSpeaking] = useState(false);

  useEffect(() => {
    if (!stageId) return;
    let cancelled = false;

    const load = async () => {
      try {
        const stage = await StageService.getStageById(Number(stageId));
        if (cancelled) return;
        const sorted = [...stage.words].sort((a, b) => a.orderIndex - b.orderIndex);
        setWords(sorted);
        setStageInfo({ difficulty: stage.difficulty, stageNumber: stage.stageNumber });
      } catch {
        if (!cancelled) setError(true);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    void load();
    return () => { cancelled = true; };
  }, [stageId]);

  useEffect(() => {
    const word = words[currentIndex];
    if (!word) return;

    setLoadingRelations(true);
    WordRelationService.getRelations(word.wordId)
      .then(setRelations)
      .catch(() => setRelations([]))
      .finally(() => setLoadingRelations(false));
  }, [currentIndex, words]);

  const speakWord = (word: string) => {
    if (!window.speechSynthesis) {
      toast.error('TTS를 지원하지 않는 브라우저입니다.');
      return;
    }

    if (!word || word.trim() === '') {
      toast.error('발음할 단어가 없습니다.');
      return;
    }

    window.speechSynthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(word);
    utterance.lang = 'en-US';
    utterance.rate = 0.9;

    utterance.onstart = () => setSpeaking(true);
    utterance.onend = () => setSpeaking(false);
    utterance.onerror = () => {
      setSpeaking(false);
      toast.error('발음 재생 중 오류가 발생했습니다.');
    };

    window.speechSynthesis.speak(utterance);
  };

  const getRelationBadgeClass = (type: string) => {
    switch (type) {
      case 'SYNONYM': return 'bg-blue-100 text-blue-700';
      case 'ANTONYM': return 'bg-red-100 text-red-700';
      case 'RELATED': return 'bg-green-100 text-green-700';
      default: return 'bg-slate-100 text-slate-700';
    }
  };

  const getRelationLabel = (type: string) => {
    switch (type) {
      case 'SYNONYM': return '유의어';
      case 'ANTONYM': return '반의어';
      case 'RELATED': return '관련어';
      default: return type;
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center flex-col gap-4">
        <LoadingSpinner />
        <p className="text-slate-500">Loading words...</p>
      </div>
    );
  }

  if (error || words.length === 0) {
    return (
      <div className="flex min-h-screen items-center justify-center flex-col gap-4 text-slate-400">
        <AlertCircle className="w-12 h-12" />
        <p className="text-lg">No words found.</p>
        <button onClick={() => navigate(-1)} className="text-indigo-600 hover:underline text-sm">
          ← Back
        </button>
      </div>
    );
  }

  const word = words[currentIndex];
  const isFirst = currentIndex === 0;
  const isLast = currentIndex === words.length - 1;

  return (
    <div className="min-h-screen bg-slate-50 p-6">
      <div className="max-w-2xl mx-auto flex flex-col gap-6">
        {/* Header */}
        <div className="grid grid-cols-3 items-center">
          <button
            onClick={() => navigate(-1)}
            className="justify-self-start rounded-full hover:bg-white p-2 transition-colors"
          >
            <ChevronLeft className="w-6 h-6" />
          </button>
          <h1 className="text-lg font-bold text-center text-slate-900 uppercase">
            {stageInfo?.difficulty.toUpperCase()} STAGE {stageInfo?.stageNumber}
          </h1>
          <p className="justify-self-end text-slate-500 text-sm">
            Word {currentIndex + 1}/{words.length}
          </p>
        </div>

        {/* Progress Bar */}
        <div className="h-3 bg-slate-200 rounded-full overflow-hidden">
          <motion.div
            className="h-full bg-indigo-600 rounded-full"
            animate={{ width: `${((currentIndex + 1) / words.length) * 100}%` }}
            transition={{ type: 'spring', stiffness: 200, damping: 30 }}
          />
        </div>

        {/* Word Card */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentIndex}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
            className="bg-white rounded-[2.5rem] shadow-xl p-12 border border-slate-100 text-center"
          >
            <p className="text-6xl font-black text-slate-900 mb-2">{word.word}</p>

            <div className="flex justify-center mb-4">
              <button
                onClick={() => speakWord(word.word)}
                disabled={speaking}
                className={`p-3 rounded-full transition-all ${
                  speaking
                    ? 'bg-indigo-100 text-indigo-600 animate-pulse'
                    : 'hover:bg-slate-100 text-slate-500 hover:text-indigo-600'
                }`}
                aria-label={`발음 듣기: ${word.word}`}
              >
                <Volume2 className="w-6 h-6" />
              </button>
            </div>

            <hr className="border-slate-100 my-6" />

            <p className="text-3xl font-bold text-slate-700 mb-3">{word.meaning}</p>

            {word.example && (
              <p className="text-sm text-slate-500 italic">{word.example}</p>
            )}
          </motion.div>
        </AnimatePresence>

        {/* Related Words */}
        <div className="bg-white rounded-2xl border border-slate-100 px-6 py-4">
          <h3 className="text-sm font-bold text-slate-700 mb-3">관련 단어</h3>
          {loadingRelations ? (
            <div className="text-center py-4 text-slate-400 text-sm">
              로딩 중...
            </div>
          ) : relations.length === 0 ? (
            <div className="text-center py-4 text-slate-400 text-sm">
              관련 단어가 없습니다
            </div>
          ) : (
            <div className="flex flex-wrap gap-2">
              {relations.map((relation) => (
                <div
                  key={relation.id}
                  className="flex items-center gap-2 px-3 py-2 rounded-lg bg-slate-50 border border-slate-100"
                >
                  <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${getRelationBadgeClass(relation.relationType)}`}>
                    {getRelationLabel(relation.relationType)}
                  </span>
                  <span className="font-medium text-slate-800">{relation.relatedWord}</span>
                  <span className="text-sm text-slate-500">{relation.relatedMeaning}</span>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Navigation */}
        <div className="flex justify-between mt-2">
          <button
            onClick={() => setCurrentIndex((prev) => prev - 1)}
            className={`bg-white border border-slate-200 rounded-xl px-6 py-3 hover:bg-slate-50 transition-colors ${
              isFirst ? 'invisible' : ''
            }`}
          >
            ← 이전
          </button>

          {isLast ? (
            <button
              onClick={() => navigate(`/quiz/${stageId}`)}
              className="bg-indigo-600 text-white rounded-xl px-6 py-3 font-semibold hover:bg-indigo-700 transition-colors"
            >
              📝 퀴즈 풀기
            </button>
          ) : (
            <button
              onClick={() => setCurrentIndex((prev) => prev + 1)}
              className="bg-white border border-slate-200 rounded-xl px-6 py-3 hover:bg-slate-50 transition-colors"
            >
              다음 →
            </button>
          )}
        </div>

        {/* Always-visible quiz button */}
        <button
          onClick={() => navigate(`/quiz/${stageId}`)}
          className="bg-indigo-600 text-white w-full rounded-full py-4 font-semibold hover:bg-indigo-700 transition-colors"
        >
          📝 퀴즈 풀기
        </button>
      </div>
    </div>
  );
}
