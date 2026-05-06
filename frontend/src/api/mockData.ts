import type { Stage, IncorrectWord } from '../types';

export const MOCK_STAGE: Stage = {
  stageId: 1,
  difficulty: 'easy',
  stageNumber: 1,
  words: [
    { wordId: 1,  stageId: 1, word: 'morning',  meaning: '아침',      example: null, imageUrl: null, orderIndex: 1  },
    { wordId: 2,  stageId: 1, word: 'afternoon',meaning: '오후',      example: null, imageUrl: null, orderIndex: 2  },
    { wordId: 3,  stageId: 1, word: 'evening',  meaning: '저녁',      example: null, imageUrl: null, orderIndex: 3  },
    { wordId: 4,  stageId: 1, word: 'night',    meaning: '밤',        example: null, imageUrl: null, orderIndex: 4  },
    { wordId: 5,  stageId: 1, word: 'door',     meaning: '문',        example: null, imageUrl: null, orderIndex: 5  },
    { wordId: 6,  stageId: 1, word: 'clock',    meaning: '시계',      example: null, imageUrl: null, orderIndex: 6  },
    { wordId: 7,  stageId: 1, word: 'watch',    meaning: '손목시계',  example: null, imageUrl: null, orderIndex: 7  },
    { wordId: 8,  stageId: 1, word: 'subway',   meaning: '지하철',    example: null, imageUrl: null, orderIndex: 8  },
    { wordId: 9,  stageId: 1, word: 'paper',    meaning: '종이',      example: null, imageUrl: null, orderIndex: 9  },
    { wordId: 10, stageId: 1, word: 'ruler',    meaning: '자',        example: null, imageUrl: null, orderIndex: 10 },
  ],
  createdAt: '2025-01-01T00:00:00.000Z',
  updatedAt: '2025-01-01T00:00:00.000Z',
};

export const MOCK_INCORRECT_WORDS: IncorrectWord[] = [
  { incorrectWordId: 1, wordId: 1, uid: 'mock-user', count: 1, lastIncorrectAt: '2025-01-01T00:00:00Z' },
  { incorrectWordId: 2, wordId: 2, uid: 'mock-user', count: 2, lastIncorrectAt: '2025-01-01T00:00:00Z' },
];
