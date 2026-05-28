import type { UserProgress } from '../types';

export type ProgressSyncStatus = 'idle' | 'queued' | 'syncing' | 'synced' | 'failed';

export interface OfflineProgressPayload extends Partial<UserProgress> {
  stageId: number;
  completed?: boolean;
}

export interface OfflineProgressItem {
  id: string;
  createdAt: string;
  attempts: number;
  payload: OfflineProgressPayload;
}

const STORAGE_KEY = 'tokki.offlineProgressQueue.v1';
const SYNC_EVENT = 'tokki:progress-sync';

function canUseStorage() {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
}

function readQueue(): OfflineProgressItem[] {
  if (!canUseStorage()) return [];
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as OfflineProgressItem[]) : [];
  } catch {
    return [];
  }
}

function writeQueue(items: OfflineProgressItem[]) {
  if (!canUseStorage()) return;
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
}

export function emitProgressSyncStatus(status: ProgressSyncStatus) {
  if (typeof window === 'undefined') return;
  window.dispatchEvent(new CustomEvent(SYNC_EVENT, { detail: { status } }));
}

export function subscribeProgressSyncStatus(listener: (status: ProgressSyncStatus) => void) {
  const handler = (event: Event) => {
    const customEvent = event as CustomEvent<{ status?: ProgressSyncStatus }>;
    listener(customEvent.detail?.status ?? 'idle');
  };
  window.addEventListener(SYNC_EVENT, handler);
  return () => window.removeEventListener(SYNC_EVENT, handler);
}

export class OfflineProgressQueue {
  static all(): OfflineProgressItem[] {
    return readQueue();
  }

  static hasPending(): boolean {
    return readQueue().length > 0;
  }

  static enqueue(payload: OfflineProgressPayload): OfflineProgressItem {
    const queue = readQueue();
    const id = `${payload.stageId}-${Date.now()}-${Math.random().toString(36).slice(2)}`;
    const nextItem: OfflineProgressItem = {
      id,
      createdAt: new Date().toISOString(),
      attempts: 0,
      payload,
    };
    const withoutSameStage = queue.filter((item) => item.payload.stageId !== payload.stageId);
    writeQueue([...withoutSameStage, nextItem]);
    emitProgressSyncStatus('queued');
    return nextItem;
  }

  static remove(id: string) {
    writeQueue(readQueue().filter((item) => item.id !== id));
  }

  static replace(item: OfflineProgressItem) {
    writeQueue(readQueue().map((existing) => (existing.id === item.id ? item : existing)));
  }

  static findByStage(stageId: number): OfflineProgressItem | null {
    return readQueue().find((item) => item.payload.stageId === stageId) ?? null;
  }
}

export const progressSyncEventName = SYNC_EVENT;
