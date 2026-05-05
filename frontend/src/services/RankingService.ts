import http from '../lib/axios';
import type { Ranking } from '../types';

export class RankingService {
  static async getRankings(): Promise<Ranking[]> {
    return (await http.get('/rankings')) as unknown as Ranking[];
  }
}
