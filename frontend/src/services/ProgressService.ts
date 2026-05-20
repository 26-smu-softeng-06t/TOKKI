import http from '../lib/axios';
import type { UserProgress, IncorrectWord } from '../types';
import {
  emitProgressSyncStatus,
  OfflineProgressQueue,
  type OfflineProgressPayload,
} from './OfflineProgressQueue';

export class ProgressService {
  static async getAllProgress(): Promise<Map<number, UserProgress>> {
    try {
      const all = (await http.get('/progress')) as unknown as UserProgress[];
      const map = new Map<number, UserProgress>(all.map((p) => [p.stageId, p]));
      mergeQueuedProgress(map);
      return map;
    } catch {
      const map = new Map<number, UserProgress>();
      mergeQueuedProgress(map);
      return map;
    }
  }

  static async getProgress(stageId: number): Promise<UserProgress | null> {
    try {
      const progresses = (await http.get('/progress')) as unknown as UserProgress[];
      return progresses.find((progress) => progress.stageId === stageId) ?? null;
    } catch (err) {
      const queued = OfflineProgressQueue.findByStage(stageId);
      if (queued) return queued.payload as UserProgress;
      const msg = err instanceof Error ? err.message : '';
      if (msg.includes('404') || msg === 'NOT_FOUND') return null;
      throw err;
    }
  }

  static async saveProgress(progress: UserProgress): Promise<void> {
    await saveOrQueue(progress);
  }

  static async markStageCompleted(stageId: number): Promise<void> {
    await saveOrQueue({ stageId, completed: true });
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

  static async syncQueuedProgress(): Promise<void> {
    await syncQueuedProgress();
  }
}

function mergeQueuedProgress(map: Map<number, UserProgress>): void {
  for (const item of OfflineProgressQueue.all()) {
    const p = item.payload;
    if (p.stageId === undefined) continue;
    // 오프라인 큐 항목은 아직 서버에 반영되지 않은 최신 로컬 상태이므로 서버 값을 덮어씀
    map.set(p.stageId, {
      id: p.id ?? 0,
      uid: p.uid ?? '',
      stageId: p.stageId,
      completed: p.completed ?? false,
      completedAt: p.completedAt ?? null,
    });
  }
}

async function saveOrQueue(payload: OfflineProgressPayload): Promise<void> {
  if (typeof navigator !== 'undefined' && !navigator.onLine) {
    OfflineProgressQueue.enqueue(payload);
    return;
  }

  try {
    await http.post('/progress', payload);
    emitProgressSyncStatus(OfflineProgressQueue.hasPending() ? 'queued' : 'synced');
  } catch (err) {
    OfflineProgressQueue.enqueue(payload);
    emitProgressSyncStatus('failed');
    throw err;
  }
}

async function syncQueuedProgress(): Promise<void> {
  if (typeof navigator !== 'undefined' && !navigator.onLine) return;
  const queue = OfflineProgressQueue.all();
  if (queue.length === 0) {
    emitProgressSyncStatus('synced');
    return;
  }

  emitProgressSyncStatus('syncing');
  for (const item of queue) {
    try {
      await http.post('/progress', item.payload);
      OfflineProgressQueue.remove(item.id);
    } catch {
      OfflineProgressQueue.replace({ ...item, attempts: item.attempts + 1 });
      emitProgressSyncStatus('failed');
      return;
    }
  }
  emitProgressSyncStatus('synced');
}

if (typeof window !== 'undefined') {
  window.addEventListener('online', () => {
    void syncQueuedProgress();
  });
}
