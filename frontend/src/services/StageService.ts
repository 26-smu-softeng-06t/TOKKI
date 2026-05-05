import http from '../lib/axios';
// [DEV-ONLY] 백엔드 없이 퀴즈 UI를 테스트할 때 사용한 mock 폴백입니다.
// 실제 API가 준비되면 아래 주석을 해제하고 getStageById 본문을 원본으로 교체하세요.
// import { MOCK_STAGE } from '../api/mockData';
import type { Stage, StageInput } from '../types';

export class StageService {
  static async getStages(): Promise<Stage[]> {
    return (await http.get('/stages')) as unknown as Stage[];
  }

  static async getStageById(stageId: string): Promise<Stage> {
    return (await http.get(`/stages/${stageId}`)) as unknown as Stage;
    // [DEV-ONLY] API 연결 전 테스트용 mock 폴백 — 필요 시 아래 주석 해제 후 위 줄을 주석 처리하세요.
    // try {
    //   return (await http.get(`/stages/${stageId}`)) as unknown as Stage;
    // } catch (err) {
    //   if (import.meta.env.DEV) {
    //     console.warn('[DEV] Stage API unavailable — using mock data');
    //     return { ...MOCK_STAGE, stageId };
    //   }
    //   throw err;
    // }
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
