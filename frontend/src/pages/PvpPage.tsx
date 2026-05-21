import { useState, useEffect, useRef, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { ChevronLeft, Sword, Users, Copy, Check, Trophy, Clock } from 'lucide-react';
import { toast } from 'sonner';
import { useAuth } from '../hooks/useAuth';
import { usePvp } from '../hooks/usePvp';
import { PvpService } from '../services/PvpService';
import { StageService } from '../services/StageService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { Stage, PvpRoom, BattleResultMessage } from '../types';

type GamePhase = 'mode-select' | 'create' | 'join' | 'waiting' | 'playing' | 'result';

export default function PvpPage() {
  const { user } = useAuth();
  const { roomCode } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [phase, setPhase] = useState<GamePhase>('mode-select');
  const [stages, setStages] = useState<Stage[]>([]);
  const [selectedStage, setSelectedStage] = useState<Stage | null>(null);
  const [room, setRoom] = useState<PvpRoom | null>(null);
  const [joinCode, setJoinCode] = useState('');
  const [copied, setCopied] = useState(false);
  const [playerCount, setPlayerCount] = useState(1);
  const [battleResult, setBattleResult] = useState<BattleResultMessage | null>(null);
  const [quizMode, setQuizMode] = useState<'EtoK' | 'KtoE'>('KtoE'); // 방장이 선택하는 모드
  const [opponentProgress, setOpponentProgress] = useState(0); // 상대방 진행률
  const hasJoinedRef = useRef(false);

  // Ref to store the latest startGame function
  const startGameRef = useRef(async () => {
    if (!room) return;
    // Only host starts the game via API
    if (room.hostUid === user?.uid) {
      try {
        await PvpService.startGame(room.id);
        setPhase('playing');
        navigate(`/quiz/${room.stageId}?pvp=true&roomId=${room.id}&roomCode=${room.roomCode}&mode=${quizMode}`);
      } catch (error) {
        console.error('Failed to start game:', error);
        toast.error('Failed to start game');
      }
    } else {
      // Guest waits for host to start
      setPhase('playing');
      navigate(`/quiz/${room.stageId}?pvp=true&roomId=${room.id}&roomCode=${room.roomCode}&mode=${quizMode}`);
    }
  });

  // Update ref when room changes
  useEffect(() => {
    startGameRef.current = async () => {
      if (!room) return;
      // Only host starts the game via API
      if (room.hostUid === user?.uid) {
        try {
          await PvpService.startGame(room.id);
          setPhase('playing');
          navigate(`/quiz/${room.stageId}?pvp=true&roomId=${room.id}&roomCode=${room.roomCode}&mode=${quizMode}`);
        } catch (error) {
          console.error('Failed to start game:', error);
          toast.error('Failed to start game');
        }
      } else {
        // Guest waits for host to start
        setPhase('playing');
        navigate(`/quiz/${room.stageId}?pvp=true&roomId=${room.id}&roomCode=${room.roomCode}&mode=${quizMode}`);
      }
    };
  }, [room, navigate, quizMode, user?.uid]);

  const { connected, disconnect: disconnectWs } = usePvp({
    roomId: room?.id ?? null,
    onProgress: (message) => {
      // 상대방 진행률 업데이트
      setOpponentProgress(message.index);
    },
    onBattleComplete: (message) => {
      setBattleResult(message);
      setPhase('result');
    },
    onRoomState: (count) => {
      setPlayerCount(count);
      if (count === 2 && phase === 'waiting') {
        startGameRef.current();
      }
    },
    onError: (error) => {
      console.error('PvP WebSocket error:', error);
      toast.error('실시간 연결에 실패했습니다. 다시 시도해주세요.');
      // 연결 실패 시 메인 페이지로 이동
      setTimeout(() => {
        navigate('/pvp');
      }, 2000);
    },
  });

  useEffect(() => {
    const loadStages = async () => {
      try {
        const allStages = await StageService.getStages();
        setStages(allStages);
      } catch {
        toast.error('Failed to load stages');
      }
    };
    loadStages();
  }, []);

  const joinRoomByCode = useCallback(async (code: string) => {
    // 이미 같은 방에 있는 경우 다시 참여하지 않음
    if (room && room.roomCode === code) {
      console.log('Already in room:', code, 'as host or guest');
      return;
    }

    try {
      console.log('Joining room with code:', code);
      const joinedRoom = await PvpService.joinRoom(code);
      console.log('Joined room:', joinedRoom);
      setRoom(joinedRoom);
      setPhase('waiting');
    } catch (error) {
      console.error('Failed to join room:', error);
      toast.error('Failed to join room');
      navigate('/pvp');
    }
  }, [navigate, room]);

  // roomCode가 변경될 때만 방 참여 시도 (중복 방지)
  useEffect(() => {
    console.log('PvpPage render - roomCode:', roomCode, 'room:', room, 'phase:', phase);
    if (roomCode && !room && !hasJoinedRef.current) {
      console.log('Triggering joinRoomByCode with:', roomCode);
      hasJoinedRef.current = true;
      joinRoomByCode(roomCode);
    } else if (!roomCode) {
      // roomCode가 없으면 ref 리셋
      hasJoinedRef.current = false;
    }
  }, [roomCode, room, joinRoomByCode]);

  // roomCode URL 파라미터가 있으면 초기 phase를 waiting으로 설정
  useEffect(() => {
    if (roomCode && phase === 'mode-select') {
      setPhase('waiting');
    } else if (!roomCode) {
      hasJoinedRef.current = false;
    }
  }, [roomCode, phase]);

  const createRoom = async () => {
    if (!selectedStage) return;

    try {
      console.log('Creating room with stageId:', selectedStage.stageId);
      const newRoom = await PvpService.createRoom(selectedStage.stageId);
      console.log('Room created:', newRoom);
      // 먼저 room을 설정해서 useEffect가 중복 참여를 방지
      hasJoinedRef.current = true;
      setRoom(newRoom);
      setPhase('waiting');
      // 그 다음 URL만 변경
      navigate(`/pvp/${newRoom.roomCode}`);
    } catch (error) {
      console.error('Create room error:', error);
      toast.error('Failed to create room');
    }
  };

  const joinByCode = () => {
    if (!joinCode.trim()) {
      toast.error('Please enter a room code');
      return;
    }
    if (joinCode.length !== 6) {
      toast.error('Room code must be 6 characters');
      return;
    }
    console.log('Navigating to room:', joinCode);
    navigate(`/pvp/${joinCode}`);
  };

  const copyInviteCode = () => {
    if (!room) return;
    navigator.clipboard.writeText(room.roomCode);
    setCopied(true);
    toast.success('Invite code copied!');
    setTimeout(() => setCopied(false), 2000);
  };

  const handleExit = () => {
    disconnectWs();
    navigate('/');
  };

  if (!user) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-purple-50 p-6">
      <div className="max-w-2xl mx-auto flex flex-col gap-6">
        {/* Header */}
        <div className="grid grid-cols-3 items-center">
          <button
            onClick={handleExit}
            aria-label="뒤로가기"
            className="justify-self-start rounded-full hover:bg-white p-2 transition-colors"
          >
            <ChevronLeft className="w-6 h-6" />
          </button>
          <h1 className="text-3xl font-bold text-center text-indigo-900 flex items-center justify-center gap-2">
            <Sword className="w-8 h-8" />
            PvP Battle
          </h1>
          <div />
        </div>

        {/* Mode Selection */}
        {phase === 'mode-select' && (
          <div className="flex flex-col gap-4">
            <button
              onClick={() => setPhase('create')}
              className="bg-white p-8 rounded-2xl shadow-md hover:shadow-lg transition-shadow border-2 border-indigo-100 hover:border-indigo-400"
            >
              <div className="flex flex-col items-center gap-4">
                <div className="w-16 h-16 bg-indigo-100 rounded-full flex items-center justify-center">
                  <Sword className="w-8 h-8 text-indigo-600" />
                </div>
                <h2 className="text-2xl font-bold text-slate-800">Create Room</h2>
                <p className="text-slate-500">Create a room and invite a friend</p>
              </div>
            </button>

            <button
              onClick={() => setPhase('join')}
              className="bg-white p-8 rounded-2xl shadow-md hover:shadow-lg transition-shadow border-2 border-purple-100 hover:border-purple-400"
            >
              <div className="flex flex-col items-center gap-4">
                <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center">
                  <Users className="w-8 h-8 text-purple-600" />
                </div>
                <h2 className="text-2xl font-bold text-slate-800">Join Room</h2>
                <p className="text-slate-500">Enter a code to join a friend's room</p>
              </div>
            </button>
          </div>
        )}

        {/* Create Room */}
        {phase === 'create' && (
          <div className="bg-white p-6 rounded-2xl shadow-md">
            <h2 className="text-xl font-bold text-slate-800 mb-4">Select Stage</h2>
            <div className="grid grid-cols-2 gap-3 max-h-96 overflow-y-auto">
              {stages.map((stage) => (
                <button
                  key={stage.stageId}
                  onClick={() => setSelectedStage(stage)}
                  className={`p-4 rounded-xl border-2 transition-all ${
                    selectedStage?.stageId === stage.stageId
                      ? 'bg-indigo-50 border-indigo-500'
                      : 'bg-slate-50 border-slate-200 hover:border-indigo-300'
                  }`}
                >
                  <p className="text-xs text-slate-500">Stage</p>
                  <p className="text-2xl font-bold">{stage.stageNumber}</p>
                  <p className="text-sm text-slate-600 capitalize">{stage.difficulty}</p>
                </button>
              ))}
            </div>
            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setPhase('mode-select')}
                className="flex-1 py-3 bg-slate-200 text-slate-700 rounded-xl font-medium hover:bg-slate-300"
              >
                Back
              </button>
              <button
                onClick={createRoom}
                disabled={!selectedStage}
                className="flex-1 py-3 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Create
              </button>
            </div>
          </div>
        )}

        {/* Join Room */}
        {phase === 'join' && (
          <div className="bg-white p-6 rounded-2xl shadow-md">
            <h2 className="text-xl font-bold text-slate-800 mb-4">Enter Room Code</h2>
            <input
              type="text"
              value={joinCode}
              onChange={(e) => setJoinCode(e.target.value.toUpperCase())}
              placeholder="Enter 6-character code"
              className="w-full p-4 text-2xl text-center font-mono tracking-widest border-2 border-slate-200 rounded-xl focus:border-indigo-500 focus:outline-none"
              maxLength={6}
            />
            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setPhase('mode-select')}
                className="flex-1 py-3 bg-slate-200 text-slate-700 rounded-xl font-medium hover:bg-slate-300"
              >
                Back
              </button>
              <button
                onClick={joinByCode}
                className="flex-1 py-3 bg-purple-600 text-white rounded-xl font-medium hover:bg-purple-700"
              >
                Join
              </button>
            </div>
          </div>
        )}

        {/* Waiting Room */}
        {phase === 'waiting' && room && (
          <div className="bg-white p-8 rounded-2xl shadow-md text-center">
            <div className="flex flex-col items-center gap-5">
              <div className="w-20 h-20 bg-indigo-100 rounded-full flex items-center justify-center">
                <Users className="w-10 h-10 text-indigo-600" />
              </div>

              <div>
                <h2 className="text-2xl font-bold text-slate-800">
                  {playerCount === 2 ? '게임 준비 완료!' : '상대방을 기다리는 중...'}
                </h2>
                <p className="text-slate-500 mt-1">
                  {playerCount === 2 ? '곧 게임이 시작됩니다' : '코드를 공유하세요'}
                </p>
              </div>

              {/* 방 코드 (방장만 표시) */}
              {room.hostUid === user?.uid && (
                <div className="bg-slate-100 px-8 py-4 rounded-xl flex items-center gap-4">
                  <span className="text-4xl font-mono font-bold text-indigo-600">{room.roomCode}</span>
                  <button
                    onClick={copyInviteCode}
                    className="p-2 hover:bg-indigo-100 rounded-lg transition-colors"
                  >
                    {copied ? <Check className="w-6 h-6 text-green-500" /> : <Copy className="w-6 h-6 text-slate-500" />}
                  </button>
                </div>
              )}

              {/* 모드 선택 (방장만, 게임 시작 전) */}
              {room.hostUid === user?.uid && playerCount < 2 && (
                <div className="w-full">
                  <p className="text-sm text-slate-600 mb-2">퀴즈 모드 선택</p>
                  <div className="flex gap-2 justify-center">
                    <button
                      onClick={() => setQuizMode('KtoE')}
                      className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                        quizMode === 'KtoE'
                          ? 'bg-indigo-600 text-white'
                          : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                      }`}
                    >
                      한➡️영
                    </button>
                    <button
                      onClick={() => setQuizMode('EtoK')}
                      className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                        quizMode === 'EtoK'
                          ? 'bg-indigo-600 text-white'
                          : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                      }`}
                    >
                      영➡️한
                    </button>
                  </div>
                </div>
              )}

              {/* 상대방 진행률 (게임 중) */}
              {playerCount === 2 && phase !== 'playing' && phase !== 'result' && (
                <div className="w-full bg-slate-50 p-4 rounded-lg">
                  <p className="text-sm text-slate-600 mb-2">상대방 진행률</p>
                  <div className="flex items-center gap-2">
                    <div className="flex-1 bg-slate-200 rounded-full h-2 overflow-hidden">
                      <div
                        className="h-full bg-purple-500 transition-all duration-300"
                        style={{ width: `${(opponentProgress / (room?.stageId ? 10 : 10)) * 100}%` }}
                      />
                    </div>
                    <span className="text-sm font-medium text-slate-600 w-12 text-right">
                      {opponentProgress}
                    </span>
                  </div>
                </div>
              )}

              <div className="flex items-center gap-4">
                <div className={`w-4 h-4 rounded-full ${connected ? 'bg-green-500' : 'bg-red-500'}`} />
                <span className="text-sm text-slate-600">
                  {connected ? '연결됨' : '연결 중...'} ({playerCount}/2 명)
                </span>
              </div>

              <div className="flex gap-2">
                {[1, 2].map((i) => (
                  <div
                    key={i}
                    className={`w-16 h-16 rounded-xl border-2 flex items-center justify-center ${
                      i <= playerCount ? 'bg-indigo-100 border-indigo-400' : 'bg-slate-100 border-slate-200 border-dashed'
                    }`}
                  >
                    {i <= playerCount ? <Users className="w-8 h-8 text-indigo-600" /> : null}
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Result */}
        {phase === 'result' && battleResult && (
          <div className="bg-white p-8 rounded-2xl shadow-md text-center">
            <div className="flex flex-col items-center gap-6">
              <div className="w-20 h-20 bg-amber-100 rounded-full flex items-center justify-center">
                <Trophy className="w-10 h-10 text-amber-600" />
              </div>

              <div>
                <h2 className="text-3xl font-bold text-slate-800">
                  {battleResult.winnerId === user?.uid ? 'You Win! 🎉' : 'You Lose 😢'}
                </h2>
                <p className="text-slate-500 mt-1">Battle Complete</p>
              </div>

              <div className="grid grid-cols-2 gap-6 w-full">
                <div className="bg-indigo-50 p-6 rounded-xl">
                  <p className="text-sm text-slate-500">Your Score</p>
                  <p className="text-3xl font-bold text-indigo-600">{battleResult.hostScore}</p>
                  <div className="flex items-center justify-center gap-2 mt-2 text-slate-600">
                    <Clock className="w-4 h-4" />
                    <span>{battleResult.hostTime}s</span>
                  </div>
                </div>

                <div className="bg-purple-50 p-6 rounded-xl">
                  <p className="text-sm text-slate-500">Opponent Score</p>
                  <p className="text-3xl font-bold text-purple-600">{battleResult.guestScore}</p>
                  <div className="flex items-center justify-center gap-2 mt-2 text-slate-600">
                    <Clock className="w-4 h-4" />
                    <span>{battleResult.guestTime}s</span>
                  </div>
                </div>
              </div>

              <button
                onClick={handleExit}
                className="w-full py-4 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700"
              >
                Back to Menu
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
