import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { AnimatePresence, motion } from 'framer-motion';
import { ChevronLeft, AlertCircle, Volume2 } from 'lucide-react';
import { toast } from 'sonner';
import { StageService } from '../services/StageService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { Word } from '../types';

export default function LearnPage() {
  const { stageId } = useParams<{ stageId: string }>();
  const navigate = useNavigate();

  const [words, setWords] = useState<Word[]>([]);
  const [stageInfo, setStageInfo] = useState<{ difficulty: string; stageNumber: number } | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

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
                onClick={() => toast.info('Phase 2에서 제공됩니다')}
                className="p-2 rounded-full hover:bg-slate-100 transition-colors text-slate-400"
                aria-label="Pronounce word"
              >
                <Volume2 className="w-5 h-5" />
              </button>
            </div>

            <hr className="border-slate-100 my-6" />

            <p className="text-3xl font-bold text-slate-700 mb-3">{word.meaning}</p>

            {word.example && (
              <p className="text-sm text-slate-500 italic">{word.example}</p>
            )}
          </motion.div>
        </AnimatePresence>

        {/* Related Words stub */}
        <div className="bg-white rounded-2xl border border-slate-100 px-6 py-4 text-center text-slate-400 text-sm">
          Related words (Phase 2)
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
