import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Play, Settings, Database, LogOut } from 'lucide-react';
import { toast } from 'sonner';
import { useAuth } from '../hooks/useAuth';
import { StageService } from '../services/StageService';
import type { StageInput, DifficultyLevel } from '../types';

const SEED_DATA: StageInput[] = (['easy', 'medium', 'hard'] as DifficultyLevel[]).flatMap(
  (difficulty) =>
    Array.from({ length: 10 }, (_, si) => ({
      difficulty,
      stageNumber: si + 1,
      words: Array.from({ length: 10 }, (_, wi) => ({
        word: `word${wi + 1}`,
        meaning: `단어${wi + 1}`,
        example: null as null,
        orderIndex: wi + 1,
      })),
    }))
);

export default function MainPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [seeding, setSeeding] = useState(false);

  if (!user) return null;

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  const handleSeed = async () => {
    setSeeding(true);
    try {
      await StageService.batchUploadStages(SEED_DATA);
      toast.success('Seed 완료!');
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Seed 실패');
    } finally {
      setSeeding(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-indigo-500 to-purple-600 text-white">
      <div className="flex flex-col items-center gap-6">
        <h1 className="text-5xl md:text-7xl font-extrabold">TOKKI</h1>
        <p className="text-indigo-100 text-lg md:text-xl text-center max-w-md">
          Challenge your vocabulary and master new words!
        </p>

        <button
          onClick={() => navigate('/select')}
          className="bg-white text-indigo-600 rounded-full px-10 py-4 font-bold hover:scale-105 transition-transform flex items-center gap-2"
        >
          <Play className="w-5 h-5" />
          Start Quiz
        </button>

        {user.role === 'admin' && (
          <>
            <button
              onClick={() => navigate('/admin')}
              className="bg-indigo-700/50 backdrop-blur-sm text-white rounded-full px-8 py-3 font-medium flex items-center gap-2 hover:bg-indigo-700/70 transition-colors"
            >
              <Settings className="w-4 h-4" />
              Admin Panel
            </button>
            <button
              onClick={handleSeed}
              disabled={seeding}
              className="bg-amber-500/50 backdrop-blur-sm text-white rounded-full px-8 py-3 font-medium flex items-center gap-2 hover:bg-amber-500/70 transition-colors disabled:opacity-60"
            >
              <Database className="w-4 h-4" />
              {seeding ? 'Seeding...' : 'Quick Seed'}
            </button>
          </>
        )}

        <button
          onClick={handleLogout}
          className="text-indigo-100/70 hover:text-white transition-colors flex items-center gap-2"
        >
          <LogOut className="w-4 h-4" />
          Logout
        </button>
      </div>

      <p className="absolute bottom-8 text-indigo-100/60 text-sm">
        Logged in as: {user.email}
      </p>
    </div>
  );
}
