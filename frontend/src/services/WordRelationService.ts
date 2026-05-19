import http from '../lib/axios';
import type { WordRelation } from '../types';

export class WordRelationService {
  static async getRelations(wordId: number): Promise<WordRelation[]> {
    return http.get(`/word-relations/${wordId}`);
  }
}
