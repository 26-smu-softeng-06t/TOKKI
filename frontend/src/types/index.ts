export type DifficultyLevel = 'easy' | 'medium' | 'hard';
export type UserRole = 'user' | 'admin';
export type QuizMode = 'EtoK' | 'KtoE';
export type RelationType = 'synonym' | 'antonym' | 'derivative' | 'phrase';
export type PvpStatus = 'waiting' | 'in_progress' | 'completed';
export type ISO8601 = string;

export interface AppUser {
  uid: string;
  email: string;
  role: UserRole;
}

export interface User {
  uid: string;
  email: string;
  role: UserRole;
  createdAt: ISO8601;
}

export interface Word {
  wordId: string;
  stageId: string;
  word: string;
  meaning: string;
  example: string | null;
  imageUrl: string | null;
  orderIndex: number;
}

export interface Stage {
  stageId: string;
  difficulty: DifficultyLevel;
  stageNumber: number;
  words: Word[];
  createdAt: ISO8601;
  updatedAt: ISO8601;
}

export interface StageInput {
  difficulty: DifficultyLevel;
  stageNumber: number;
  words: WordInput[];
}

export interface WordInput {
  word: string;
  meaning: string;
  example: string | null;
  orderIndex: number;
}

export interface IncorrectWord {
  incorrectWordId: string;
  progressId: string;
  wordId: string;
  isResolved: boolean;
}

export interface UserProgress {
  progressId: string;
  userId: string;
  stageId: string;
  completed: boolean;
  lastScore: number;
  incorrectWords: IncorrectWord[];
  updatedAt: ISO8601;
}

export interface QuizAnswer {
  answerId: string;
  sessionId: string;
  wordId: string;
  userAnswer: string;
  isCorrect: boolean;
}

export interface QuizSession {
  sessionId: string;
  userId: string;
  stageId: string;
  mode: QuizMode;
  currentIndex: number;
  answers: QuizAnswer[];
  savedAt: ISO8601;
}

export interface WordRelation {
  relationId: string;
  wordId: string;
  relationType: RelationType;
  relatedWord: string;
  relatedMeaning: string;
}

export interface Ranking {
  rankingId: string;
  wordId: string;
  word: string;
  meaning: string;
  missCount: number;
  rank: number;
  updatedAt: ISO8601;
}

export interface PvpRoom {
  roomId: string;
  hostId: string;
  guestId: string | null;
  inviteCode: string;
  stageId: string;
  status: PvpStatus;
  createdAt: ISO8601;
}

export interface PvpResult {
  resultId: string;
  roomId: string;
  userId: string;
  score: number;
  completionTime: number;
}

export interface ProgressMessage {
  userId: string;
  index: number;
  score: number;
}

export interface BattleResultMessage {
  winnerId: string;
  hostScore: number;
  guestScore: number;
  hostTime: number;
  guestTime: number;
}
