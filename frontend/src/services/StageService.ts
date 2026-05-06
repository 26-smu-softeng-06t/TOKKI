import http from '../lib/axios';
import { MOCK_STAGE } from '../api/mockData';
import type { Stage, DifficultyLevel, StageInput } from '../types';

/**
 * Normalizes raw API responses into the frontend Stage type.
 * Handles two backend formats:
 *  - New schema  : { stageId, difficulty, stageNumber, createdAt, updatedAt }
 *                  words: { wordId, stageId, word, meaning, example, orderIndex }
 *  - Old backend : { id, title, description, level, createdAt }
 *                  words: { id, stageId, korean, meaning, example, imageUrl }
 *    (old backend: `korean` = Korean text ≡ frontend `meaning`;
 *                  `meaning` = English text ≡ frontend `word`)
 */
function normalizeStage(
  stageRaw: unknown,
  stageId: number,
  wordsRaw: unknown,
): Stage {
  const s = stageRaw as Record<string, unknown>;
  const ws = ((wordsRaw as unknown[]) ?? []) as Record<string, unknown>[];

  return {
    stageId: Number(s.stageId ?? s.id ?? stageId),
    difficulty: (s.difficulty ?? 'easy') as DifficultyLevel,
    stageNumber: Number(s.stageNumber ?? s.level ?? 0),
    createdAt: String(s.createdAt ?? ''),
    updatedAt: String(s.updatedAt ?? s.createdAt ?? ''),
    words: ws.map((w, i) => ({
      wordId: Number(w.wordId ?? w.id ?? 0),
      stageId: Number(w.stageId ?? stageId),
      // New schema has `word` (English); old backend has `meaning` (English) + `korean` (Korean)
      word: String(w.word ?? ('korean' in w ? w.meaning : '') ?? ''),
      meaning: String(('korean' in w ? w.korean : w.meaning) ?? ''),
      example: (w.example as string | null) ?? null,
      orderIndex: Number(w.orderIndex ?? w.order_index ?? i + 1),
    })),
  };
}

export class StageService {
  static async getStages(): Promise<Stage[]> {
    return (await http.get('/stages')) as unknown as Stage[];
  }

  static async getStageById(stageId: number): Promise<Stage> {
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

  static async createStage(data: StageInput): Promise<number> {
    const res = (await http.post('/stages', data)) as unknown as { stageId: number };
    return res.stageId;
  }

  static async updateStage(stageId: number, data: Partial<StageInput>): Promise<void> {
    await http.put(`/stages/${stageId}`, data);
  }

  static async deleteStage(stageId: number): Promise<void> {
    await http.delete(`/stages/${stageId}`);
  }

  static async batchUploadStages(stages: StageInput[]): Promise<void> {
    await http.post('/stages/batch', { stages });
  }
}
