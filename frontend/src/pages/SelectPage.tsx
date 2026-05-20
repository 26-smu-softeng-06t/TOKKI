import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, AlertCircle, Check } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { StageService } from '../services/StageService';
import { ProgressService } from '../services/ProgressService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { Stage, UserProgress, DifficultyLevel } from '../types';

const DIFFICULTIES: DifficultyLevel[] = ['easy', 'medium', 'hard'];
const DIFFICULTY_LABELS: Record<DifficultyLevel, string> = {
  easy: 'Easy',
  medium: 'Medium',
  hard: 'Hard',
};

export default function SelectPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stages, setStages] = useState<Stage[]>([]);
  const [progresses, setProgresses] = useState<Map<number, UserProgress>>(new Map());
  const [selectedDifficulty, setSelectedDifficulty] = useState<DifficultyLevel>('easy');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) return;
    let cancelled = false;

    const load = async () => {
      try {
        const [allStages, progressMap] = await Promise.all([
          StageService.getStages(),
          ProgressService.getAllProgress().catch(() => new Map<number, UserProgress>()),
        ]);
        if (cancelled) return;
        setStages(allStages);
        setProgresses(progressMap);
      } catch {
        // network error — show empty state
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    void load();
    return () => {
      cancelled = true;
    };
  }, [user]);

  const filteredStages = stages.filter((s) => s.difficulty === selectedDifficulty);
  const completedCount = stages.filter((s) => progresses.get(s.stageId)?.completed).length;
  const reviewCount = 0;

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center flex-col gap-4">
        <LoadingSpinner />
        <p className="text-slate-500">Loading stages...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 p-6">
      <div className="max-w-4xl mx-auto flex flex-col gap-6">
        {/* Header */}
        <div className="grid grid-cols-3 items-center">
          <button
            onClick={() => navigate(-1)}
            className="justify-self-start rounded-full hover:bg-white p-2 transition-colors"
          >
            <ChevronLeft className="w-6 h-6" />
          </button>
          <h1 className="text-3xl font-bold text-center text-slate-900">Select Stage</h1>
          <div />
        </div>

        {/* Difficulty Selector */}
        <div className="bg-slate-200 rounded-xl p-1 flex gap-1 max-w-md mx-auto w-full">
          {DIFFICULTIES.map((d) => (
            <button
              key={d}
              onClick={() => setSelectedDifficulty(d)}
              className={`flex-1 py-2 text-sm font-medium rounded-lg transition-all ${
                selectedDifficulty === d
                  ? 'bg-white text-indigo-600 shadow-sm'
                  : 'text-slate-500'
              }`}
            >
              {DIFFICULTY_LABELS[d]}
            </button>
          ))}
        </div>

        {/* Stage Grid */}
        {filteredStages.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 gap-4 text-slate-400">
            <AlertCircle className="w-12 h-12" />
            <p className="text-lg">No stages found.</p>
            {user?.role === 'admin' && (
              <button
                onClick={() => navigate('/admin')}
                className="text-indigo-600 hover:underline text-sm"
              >
                Go to Admin Panel
              </button>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
            {filteredStages.map((stage) => {
              const progress = progresses.get(stage.stageId);
              const isCompleted = progress?.completed ?? false;
              const hasReview = false;

              return (
                <button
                  key={stage.stageId}
                  onClick={() => navigate(`/learn/${stage.stageId}`)}
                  className={`aspect-square rounded-2xl border-2 relative cursor-pointer transition-all ${
                    isCompleted
                      ? 'bg-indigo-50 border-indigo-200 text-indigo-700'
                      : 'bg-white border-slate-200 hover:border-indigo-400 hover:shadow-md'
                  }`}
                >
                  <div className="flex flex-col items-center justify-center h-full gap-1">
                    <span className="text-xs opacity-60">Stage</span>
                    <span className="text-3xl font-bold">{stage.stageNumber}</span>
                  </div>

                  {isCompleted && (
                    <div className="absolute top-2 right-2 bg-green-500 rounded-full w-6 h-6 flex items-center justify-center">
                      <Check className="w-3 h-3 text-white" />
                    </div>
                  )}

                  {hasReview && (
                    <div className="absolute bottom-2 left-2 bg-amber-500 rounded-full px-2 py-0.5 text-[10px] text-white font-medium">
                      Review
                    </div>
                  )}
                </button>
              );
            })}
          </div>
        )}

        {/* Progress Summary */}
        <div className="bg-white p-6 rounded-2xl border shadow-sm">
          <div className="grid grid-cols-3 text-center gap-4">
            <div>
              <p className="text-3xl font-bold text-indigo-600">{completedCount}</p>
              <p className="text-sm text-slate-500 mt-1">완료</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-amber-500">{reviewCount}</p>
              <p className="text-sm text-slate-500 mt-1">복습 필요</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-slate-600">{stages.length}</p>
              <p className="text-sm text-slate-500 mt-1">총 스테이지</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
