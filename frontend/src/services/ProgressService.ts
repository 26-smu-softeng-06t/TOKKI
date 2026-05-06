import http from '../lib/axios';
import type { UserProgress, IncorrectWord } from '../types';

export class ProgressService {
  static async getProgress(stageId: number): Promise<UserProgress | null> {
    try {
      return (await http.get('/progress', { params: { stageId } })) as unknown as UserProgress;
    } catch (err) {
      const msg = err instanceof Error ? err.message : '';
      if (msg.includes('404') || msg === 'NOT_FOUND') return null;
      throw err;
    }
  }

  static async saveProgress(progress: UserProgress): Promise<void> {
    await http.post('/progress', progress);
  }

  static async markStageCompleted(stageId: number): Promise<void> {
    await http.post('/progress', { stageId, completed: true });
  }

  static async getIncorrectWords(): Promise<IncorrectWord[]> {
    try {
      return (await http.get('/progress/incorrect-words')) as unknown as IncorrectWord[];
    } catch (err) {
      if (import.meta.env.DEV) {
        const { MOCK_INCORRECT_WORDS } = await import('../api/mockData');
        return MOCK_INCORRECT_WORDS;
      }
      throw err;
    }
  }

  static async resolveIncorrectWord(wordId: number): Promise<void> {
    await http.delete(`/progress/incorrect/${wordId}`);
  }
}
