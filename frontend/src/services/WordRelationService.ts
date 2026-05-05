import http from '../lib/axios';
import type { WordRelation } from '../types';

export class WordRelationService {
  static async getRelations(wordId: string): Promise<WordRelation[]> {
    return (await http.get(`/word-relations/${wordId}`)) as unknown as WordRelation[];
  }
}
