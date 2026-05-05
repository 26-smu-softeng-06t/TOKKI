import http from '../lib/axios';
import type { PvpRoom, PvpResult } from '../types';

export class PvpService {
  private static readonly _http = http;

  static async createRoom(_stageId: string): Promise<PvpRoom> {
    throw new Error('not implemented');
  }

  static async joinRoom(_inviteCode: string): Promise<PvpRoom> {
    throw new Error('not implemented');
  }

  static async saveResult(_result: Omit<PvpResult, 'resultId'>): Promise<PvpResult> {
    throw new Error('not implemented');
  }

  static async completeRoom(_roomId: string): Promise<PvpRoom> {
    throw new Error('not implemented');
  }
}
