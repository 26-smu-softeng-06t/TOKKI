import apiClient from '../lib/axios';

export class BatchService {
  static async importWords(stageId: string, words: { english: string; korean: string }[]): Promise<void> {
    return apiClient.post('/admin/batch/words', { stageId, words });
  }
}
