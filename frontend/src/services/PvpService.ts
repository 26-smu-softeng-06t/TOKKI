import http from '../lib/axios';
import type { PvpRoom, PvpResult } from '../types';

export class PvpService {
  static async getWaitingRooms(): Promise<PvpRoom[]> {
    return (await http.get('/pvp/rooms')) as unknown as PvpRoom[];
  }

  static async createRoom(stageId: number): Promise<PvpRoom> {
    return (await http.post('/pvp/rooms', { stageId })) as unknown as PvpRoom;
  }

  static async joinRoom(roomId: number): Promise<PvpRoom> {
    return (await http.post('/pvp/rooms/join', { roomId })) as unknown as PvpRoom;
  }

  static async saveResult(result: Omit<PvpResult, 'resultId'>): Promise<PvpResult> {
    return (await http.post('/pvp/results', result)) as unknown as PvpResult;
  }

  static async completeRoom(roomId: number): Promise<PvpRoom> {
    return (await http.patch(`/pvp/rooms/${roomId}/complete`)) as unknown as PvpRoom;
  }
}
