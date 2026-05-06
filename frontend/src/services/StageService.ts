import http from '../lib/axios';
import { MOCK_STAGE } from '../api/mockData';
import type { Stage, DifficultyLevel, StageInput } from '../types';

/**
 * Normalizes raw API responses into the frontend Stage type.
 * Backend words use the same contract as the frontend:
 * { wordId, stageId, word, meaning, example, imageUrl, orderIndex }.
 */
function normalizeStage(
  stageRaw: unknown,
  stageId: string,
  wordsRaw: unknown,
): Stage {
  const s = stageRaw as Record<string, unknown>;
  const ws = ((wordsRaw as unknown[]) ?? []) as Record<string, unknown>[];

  return {
    stageId: String(s.stageId ?? s.id ?? stageId),
    difficulty: (s.difficulty ?? 'easy') as DifficultyLevel,
    stageNumber: Number(s.stageNumber ?? s.level ?? 0),
    createdAt: String(s.createdAt ?? ''),
    updatedAt: String(s.updatedAt ?? s.createdAt ?? ''),
    words: ws.map((w, i) => ({
      wordId: String(w.wordId ?? ''),
      stageId: String(w.stageId ?? stageId),
      word: String(w.word ?? ''),
      meaning: String(w.meaning ?? ''),
      example: (w.example as string | null) ?? null,
      imageUrl: (w.imageUrl as string | null) ?? null,
      orderIndex: Number(w.orderIndex ?? w.order_index ?? i + 1),
    })),
  };
}

export class StageService {
  static async getStages(): Promise<Stage[]> {
    return (await http.get('/stages')) as unknown as Stage[];
  }

  static async getStageById(stageId: string): Promise<Stage> {
    try {
      const [stageRaw, wordsRaw] = await Promise.all([
        http.get(`/stages/${stageId}`),
        http.get(`/stages/${stageId}/words`),
      ]);
      return normalizeStage(stageRaw, stageId, wordsRaw);
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
