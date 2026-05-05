import http from '../lib/axios';
import type { WordRelation } from '../types';

export class WordRelationService {
  private static readonly _http = http;

  static async getRelations(_wordId: string): Promise<WordRelation[]> {
    throw new Error('not implemented');
  }
}
