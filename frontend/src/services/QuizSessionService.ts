import http from '../lib/axios';

export interface SaveSessionRequest {
  stageId: number;
  score: number;
  totalQuestions: number;
  answers: Array<{ wordId: number; userAnswer: string; correct: boolean }>;
}

export class QuizSessionService {
  static async saveQuizResult(payload: SaveSessionRequest): Promise<void> {
    await http.post('/sessions', payload);
  }
}
