import apiClient from '../lib/axios';

export class BatchService {
  static async importWords(stageId: string, words: { word: string; meaning: string }[]): Promise<void> {
    return apiClient.post('/admin/batch/words', { stageId, words });
  }
}
