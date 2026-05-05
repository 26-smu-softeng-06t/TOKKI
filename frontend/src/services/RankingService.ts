import http from '../lib/axios';
import type { Ranking } from '../types';

export class RankingService {
  private static readonly _http = http;

  static async getRankings(): Promise<Ranking[]> {
    throw new Error('not implemented');
  }
}
