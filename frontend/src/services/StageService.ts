import http from '../lib/axios';
import type { Stage, StageInput } from '../types';

export class StageService {
  static async getStages(): Promise<Stage[]> {
    return (await http.get('/stages')) as unknown as Stage[];
  }

  static async getStageById(stageId: string): Promise<Stage> {
    return (await http.get(`/stages/${stageId}`)) as unknown as Stage;
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
