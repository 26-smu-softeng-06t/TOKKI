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
  wordId: number;
  stageId: number;
  word: string;
  meaning: string;
  example: string | null;
  orderIndex: number;
}

export interface Stage {
  stageId: number;
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
  incorrectWordId: number;
  wordId: number;
  uid: string;
  count: number;
  lastIncorrectAt: ISO8601;
}

export interface UserProgress {
  id: number;
  uid: string;
  stageId: number;
  completed: boolean;
  completedAt: ISO8601 | null;
}

export interface QuizAnswer {
  id: number;
  sessionId: number;
  wordId: number;
  userAnswer: string;
  correct: boolean;
}

export interface QuizSession {
  id: number;
  uid: string;
  stageId: number;
  score: number;
  totalQuestions: number;
  startedAt: ISO8601;
  completedAt: ISO8601 | null;
}

export interface WordRelation {
  id: number;
  wordId: number;
  relatedWordId: number;
  relationType: string;
}

export interface Ranking {
  id: number;
  uid: string;
  score: number;
  rankPosition: number;
  period: string;
}

export interface PvpRoom {
  id: number;
  hostUid: string;
  guestUid: string | null;
  stageId: number;
  status: string;
  createdAt: ISO8601;
}

export interface PvpResult {
  id: number;
  roomId: number;
  uid: string;
  score: number;
  result: string;
  createdAt: ISO8601;
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
