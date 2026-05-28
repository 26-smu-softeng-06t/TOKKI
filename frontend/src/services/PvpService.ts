import http from '../lib/axios';
import type { PvpRoom, PvpResult } from '../types';

export class PvpService {
  static async getWaitingRooms(): Promise<PvpRoom[]> {
    return (await http.get('/pvp/rooms')) as unknown as PvpRoom[];
  }

  static async createRoom(stageId: number): Promise<PvpRoom> {
    return (await http.post('/pvp/rooms', { stageId })) as unknown as PvpRoom;
  }

  static async joinRoom(roomCode: string): Promise<PvpRoom> {
    return (await http.post(`/pvp/rooms/join/${roomCode}`)) as unknown as PvpRoom;
  }

  static async startGame(roomId: number): Promise<PvpRoom> {
    return (await http.post(`/pvp/rooms/${roomId}/start`)) as unknown as PvpRoom;
  }

  static async saveResult(result: Omit<PvpResult, 'resultId'>): Promise<PvpResult> {
    return (await http.post('/pvp/results', result)) as unknown as PvpResult;
  }

  static async completeRoom(roomId: number): Promise<PvpRoom> {
    return (await http.post(`/pvp/rooms/${roomId}/complete`, null)) as unknown as PvpRoom;
  }
}
