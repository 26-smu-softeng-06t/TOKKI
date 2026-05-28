import http from '../lib/axios';
import type { Ranking } from '../types';

export class RankingService {
  static async getRankings(period?: string): Promise<Ranking[]> {
    return http.get('/rankings', { params: { period } });
  }
}
