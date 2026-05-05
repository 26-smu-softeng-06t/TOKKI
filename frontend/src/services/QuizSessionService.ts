import http from '../lib/axios';
import type { QuizSession } from '../types';

export class QuizSessionService {
  private static readonly _http = http;

  static async getSession(_stageId: string): Promise<QuizSession | null> {
    throw new Error('not implemented');
  }

  static async saveSession(_session: Partial<QuizSession>): Promise<QuizSession> {
    throw new Error('not implemented');
  }

  static async deleteSession(_sessionId: string): Promise<void> {
    throw new Error('not implemented');
  }
}
