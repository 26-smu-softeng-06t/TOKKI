import http from '../lib/axios';
import type { UserProgress, IncorrectWord } from '../types';

export class ProgressService {
  static async getProgress(userId: string, stageId: string): Promise<UserProgress | null> {
    try {
      return (await http.get('/progress', { params: { userId, stageId } })) as unknown as UserProgress;
    } catch (err) {
      const msg = err instanceof Error ? err.message : '';
      if (msg.includes('404') || msg === 'NOT_FOUND') return null;
      throw err;
    }
  }

  static async saveProgress(progress: UserProgress): Promise<void> {
    await http.post('/progress', progress);
  }

  static async getIncorrectWords(userId: string): Promise<IncorrectWord[]> {
    return (await http.get('/progress/incorrect', { params: { userId } })) as unknown as IncorrectWord[];
  }

  static async resolveIncorrectWord(progressId: string, wordId: string): Promise<void> {
    await http.patch(`/progress/${progressId}/incorrect/${wordId}`);
  }
}
