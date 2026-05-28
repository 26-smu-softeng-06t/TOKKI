import http from '../lib/axios';

export interface SaveSessionRequest {
  stageId: number;
  score: number;
  totalQuestions: number;
  answers: Array<{ wordId: number; userAnswer: string; correct: boolean }>;
}

export interface UpsertDraftSessionRequest {
  stageId: number;
  mode: 'KtoE' | 'EtoK';
  currentIndex: number;
  score: number;
  totalQuestions: number;
  answers: Array<{ wordId: number; userAnswer: string; correct: boolean }>;
}

export interface QuizSession {
  id: number;
  stageId: number;
  mode: string | null;
  currentIndex: number | null;
  score: number;
  totalQuestions: number;
  startedAt: string;
  completedAt: string | null;
}

export interface QuizSessionDetail extends QuizSession {
  answers: Array<{
    wordId: number;
    word: string;
    meaning: string;
    userAnswer: string;
    correct: boolean;
  }>;
}

export class QuizSessionService {
  static async saveQuizResult(payload: SaveSessionRequest): Promise<void> {
    await http.post('/sessions', payload);
  }

  static async upsertDraftSession(payload: UpsertDraftSessionRequest): Promise<QuizSession> {
    return http.put('/sessions/current', payload);
  }

  static async deleteDraftSession(stageId: number): Promise<void> {
    await http.delete('/sessions/current', { params: { stageId } });
  }

  static async getSessions(stageId?: number, completed?: boolean): Promise<QuizSession[]> {
    return http.get('/sessions', { params: { stageId, completed } });
  }

  static async getSessionDetail(id: number): Promise<QuizSessionDetail> {
    return http.get(`/sessions/${id}`);
  }

  static async getDraftSessions(): Promise<QuizSession[]> {
    return http.get('/sessions', { params: { completed: false } });
  }

  static async getCompletedSessions(stageId?: number): Promise<QuizSession[]> {
    return http.get('/sessions', { params: { stageId, completed: true } });
  }
}
