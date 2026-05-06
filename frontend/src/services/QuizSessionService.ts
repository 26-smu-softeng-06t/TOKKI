import http from '../lib/axios';
import type { QuizSession } from '../types';

export interface SaveSessionRequest {
  stageId: number;
  score: number;
  totalQuestions: number;
  answers: Array<{ wordId: number; userAnswer: string; correct: boolean }>;
}

export class QuizSessionService {
  static async getSession(stageId: number): Promise<QuizSession | null> {
    const sessions = (await http.get('/sessions')) as unknown as QuizSession[];
    return sessions.find((session) => session.stageId === stageId) ?? null;
  }

  static async saveSession(session: Partial<QuizSession>): Promise<QuizSession> {
    return (await http.post('/sessions', session)) as unknown as QuizSession;
  }

  static async saveQuizResult(payload: SaveSessionRequest): Promise<void> {
    await http.post('/sessions', payload);
  }

  static async deleteSession(sessionId: number): Promise<void> {
    await http.delete(`/sessions/${sessionId}`);
  }
}
