import http from '../lib/axios';
import { MOCK_STAGE } from '../api/mockData';
import type { Stage, Word, StageInput } from '../types';

export class StageService {
  static async getStages(): Promise<Stage[]> {
    return (await http.get('/stages')) as unknown as Stage[];
  }

  static async getStageById(stageId: string): Promise<Stage> {
    try {
      const [stageData, wordsData] = await Promise.all([
        http.get(`/stages/${stageId}`),
        http.get(`/stages/${stageId}/words`),
      ]);
      return { ...(stageData as unknown as Stage), words: wordsData as unknown as Word[] };
    } catch (err) {
      if (import.meta.env.DEV) {
        console.warn('[DEV] Stage API unavailable — using mock data');
        return { ...MOCK_STAGE, stageId };
      }
      throw err;
    }
  }

  static async createStage(data: StageInput): Promise<string> {
    const res = (await http.post('/stages', data)) as unknown as { stageId: string };
    return res.stageId;
  }

  static async updateStage(stageId: string, data: Partial<StageInput>): Promise<void> {
    await http.put(`/stages/${stageId}`, data);
  }

  static async deleteStage(stageId: string): Promise<void> {
    await http.delete(`/stages/${stageId}`);
  }

  static async batchUploadStages(stages: StageInput[]): Promise<void> {
    await http.post('/stages/batch', { stages });
  }
}
