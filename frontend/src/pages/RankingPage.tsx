import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Trophy, TrendingUp, AlertCircle, ArrowLeft, Calendar } from 'lucide-react';
import { RankingService } from '../services/RankingService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { Ranking } from '../types';

type Phase = 'loading' | 'error' | 'empty' | 'loaded';

export default function RankingPage() {
  const navigate = useNavigate();

  const [phase, setPhase] = useState<Phase>('loading');
  const [rankings, setRankings] = useState<Ranking[]>([]);
  const [period, setPeriod] = useState(() => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  });
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        setPhase('loading');
        const data = await RankingService.getRankings(period);
        if (data.length === 0) {
          setPhase('empty');
        } else {
          setRankings(data);
          setPhase('loaded');
        }
      } catch (err) {
        setPhase('error');
        setErrorMsg('랭킹 데이터를 불러오지 못했습니다.');
      }
    };

    void load();
  }, [period]);

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return (
          <div className="w-8 h-8 rounded-full bg-yellow-400 flex items-center justify-center text-yellow-900 font-bold text-sm shadow-lg">
            1
          </div>
        );
      case 2:
        return (
          <div className="w-8 h-8 rounded-full bg-slate-300 flex items-center justify-center text-slate-700 font-bold text-sm shadow-lg">
            2
          </div>
        );
      case 3:
        return (
          <div className="w-8 h-8 rounded-full bg-amber-600 flex items-center justify-center text-amber-100 font-bold text-sm shadow-lg">
            3
          </div>
        );
      default:
        return (
          <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-slate-500 font-bold text-sm">
            {rank}
          </div>
        );
    }
  };

  const getRankBadgeClass = (rank: number) => {
    switch (rank) {
      case 1: return 'bg-gradient-to-r from-yellow-400 to-amber-400 text-yellow-900 border-yellow-500';
      case 2: return 'bg-gradient-to-r from-slate-200 to-slate-300 text-slate-700 border-slate-400';
      case 3: return 'bg-gradient-to-r from-amber-500 to-amber-600 text-amber-100 border-amber-700';
      default: return 'bg-white text-slate-700 border-slate-200';
    }
  };

  // ── Loading ───────────────────────────────────────────────

  if (phase === 'loading') {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center flex-col gap-4">
        <LoadingSpinner />
        <p className="text-slate-500">랭킹 데이터를 불러오는 중...</p>
      </div>
    );
  }

  // ── Error ─────────────────────────────────────────────────

  if (phase === 'error') {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-8 max-w-sm w-full text-center">
          <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-4" />
          <p className="text-slate-900 font-semibold mb-2">오류가 발생했습니다</p>
          <p className="text-slate-500 text-sm mb-6">{errorMsg}</p>
          <button
            onClick={() => navigate(-1)}
            className="w-full py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold transition-colors"
          >
            돌아가기
          </button>
        </div>
      </div>
    );
  }

  // ── Empty ─────────────────────────────────────────────────

  if (phase === 'empty') {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-8 max-w-sm w-full text-center">
          <Trophy className="w-12 h-12 text-slate-300 mx-auto mb-4" />
          <p className="text-slate-900 font-semibold mb-2">랭킹 데이터가 없습니다</p>
          <p className="text-slate-500 text-sm mb-6">
            아직 오답 데이터가 충분하지 않습니다.<br />
            퀴즈를 더 풀어보세요!
          </p>
          <button
            onClick={() => navigate('/select')}
            className="w-full py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold transition-colors"
          >
            스테이지 선택으로 가기
          </button>
        </div>
      </div>
    );
  }

  // ── Loaded ────────────────────────────────────────────────

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <div className="bg-white border-b border-slate-100 sticky top-0 z-10">
        <div className="max-w-lg mx-auto px-4 py-4 flex items-center justify-between">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center gap-1 text-slate-400 hover:text-slate-700 transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
            <span className="text-sm">이전</span>
          </button>

          <div className="flex items-center gap-2">
            <Trophy className="w-5 h-5 text-amber-500" />
            <h1 className="text-lg font-bold text-slate-900">오답 TOP 10</h1>
          </div>

          <div className="w-12" />
        </div>
      </div>

      {/* Content */}
      <div className="max-w-lg mx-auto px-4 py-6">
        {/* Period Selector */}
        <div className="bg-white rounded-xl border border-slate-200 p-4 mb-6 flex items-center justify-between">
          <div className="flex items-center gap-2 text-slate-700">
            <Calendar className="w-4 h-4" />
            <span className="text-sm font-medium">기준일</span>
          </div>
          <input
            type="date"
            value={period}
            onChange={(e) => setPeriod(e.target.value)}
            max={new Date().toISOString().split('T')[0]}
            className="px-3 py-1.5 rounded-lg border border-slate-200 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Info Box */}
        <div className="bg-indigo-50 border border-indigo-100 rounded-xl p-4 mb-6">
          <div className="flex items-start gap-3">
            <TrendingUp className="w-5 h-5 text-indigo-600 mt-0.5 shrink-0" />
            <div>
              <p className="text-sm font-semibold text-indigo-900 mb-1">
                가장 많이 틀린 단어 TOP 10
              </p>
              <p className="text-xs text-indigo-700">
                선택한 날짜를 기준으로 누적 오답 횟수가 많은 순서대로 보여줍니다.
                이 단어들을 집중적으로 연습하면 효율적으로 학습할 수 있어요!
              </p>
            </div>
          </div>
        </div>

        {/* Ranking List */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          {rankings.map((item, index) => (
            <div
              key={item.id}
              className={`flex items-center gap-4 p-4 ${
                index !== rankings.length - 1 ? 'border-b border-slate-100' : ''
              }`}
            >
              {/* Rank */}
              <div className="shrink-0">
                {getRankIcon(item.rank)}
              </div>

              {/* Word Info */}
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <span className={`text-xs font-semibold px-2 py-0.5 rounded-full border ${getRankBadgeClass(item.rank)}`}>
                    TOP {item.rank}
                  </span>
                  {index < 3 && (
                    <span className="text-xs text-slate-400">
                      {item.missCount}회 오답
                    </span>
                  )}
                </div>
                <p className="text-lg font-bold text-slate-900 truncate">{item.word}</p>
                <p className="text-sm text-slate-600 truncate">{item.meaning}</p>
              </div>

              {/* Miss Count */}
              <div className="shrink-0 text-right">
                <p className="text-2xl font-black text-red-500">{item.missCount}</p>
                <p className="text-xs text-slate-400">오답</p>
              </div>
            </div>
          ))}
        </div>

        {/* Footer Note */}
        <div className="mt-6 text-center">
          <p className="text-xs text-slate-400">
            * 랭킹은 매일 자정에 업데이트됩니다
          </p>
        </div>
      </div>
    </div>
  );
}
