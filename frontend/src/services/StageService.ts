import http from '../lib/axios';
import { MOCK_STAGE } from '../api/mockData';
import type { Stage, DifficultyLevel, StageInput, ExcelUploadPreview } from '../types';

function normalizeDifficulty(value: unknown): DifficultyLevel {
  const raw = String(value ?? 'easy').toLowerCase();
  if (raw === 'low') return 'easy';
  if (raw === 'high') return 'hard';
  if (raw === 'medium') return 'medium';
  if (raw === 'hard') return 'hard';
  return 'easy';
}

/**
 * Normalizes raw API responses into the frontend Stage type.
 * Backend words use the same contract as the frontend:
 * { wordId, stageId, word, meaning, example, imageUrl, orderIndex }.
 */
function normalizeStage(
  stageRaw: unknown,
  stageId: number,
  wordsRaw: unknown,
): Stage {
  const s = stageRaw as Record<string, unknown>;
  const ws = ((wordsRaw as unknown[]) ?? []) as Record<string, unknown>[];

  return {
    stageId: Number(s.stageId ?? stageId),
    difficulty: normalizeDifficulty(s.difficulty),
    stageNumber: Number(s.stageNumber ?? 0),
    createdAt: String(s.createdAt ?? ''),
    updatedAt: String(s.updatedAt ?? s.createdAt ?? ''),
    words: ws.map((w, i) => ({
      wordId: Number(w.wordId ?? w.id ?? 0),
      stageId: Number(w.stageId ?? stageId),
      word: String(w.word ?? ''),
      meaning: String(w.meaning ?? ''),
      example: (w.example as string | null) ?? null,
      imageUrl: (w.imageUrl as string | null) ?? null,
      orderIndex: Number(w.orderIndex ?? w.order_index ?? i + 1),
    })),
  };
}

export class StageService {
  static async getStages(filters?: {
    difficulty?: DifficultyLevel;
    stageNumber?: number;
  }): Promise<Stage[]> {
    const params = filters
      ? Object.fromEntries(
          Object.entries(filters).filter(([, value]) => value !== undefined)
        )
      : undefined;
    const stages = (await http.get('/stages', { params })) as unknown as Array<Record<string, unknown>>;
    return Promise.all(
      stages.map(async (stageRaw) => {
        const stageId = Number(stageRaw.stageId ?? stageRaw.id ?? 0);
        const wordsRaw = stageId > 0 ? await http.get(`/stages/${stageId}/words`) : [];
        return normalizeStage(stageRaw, stageId, wordsRaw);
      }),
    );
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

  static async previewExcelUpload(file: File): Promise<ExcelUploadPreview> {
    const formData = new FormData();
    formData.append('file', file);
    return (await http.post('/stages/excel/preview', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })) as unknown as ExcelUploadPreview;
  }
}
