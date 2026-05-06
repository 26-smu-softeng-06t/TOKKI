import type { Stage } from '../types';

export const MOCK_STAGE: Stage = {
  stageId: 'mock-stage-1',
  difficulty: 'easy',
  stageNumber: 1,
  words: [
    { wordId: 'w1',  stageId: 'mock-stage-1', word: 'morning',  meaning: '아침',      example: null, orderIndex: 1  },
    { wordId: 'w2',  stageId: 'mock-stage-1', word: 'afternoon',meaning: '오후',      example: null, orderIndex: 2  },
    { wordId: 'w3',  stageId: 'mock-stage-1', word: 'evening',  meaning: '저녁',      example: null, orderIndex: 3  },
    { wordId: 'w4',  stageId: 'mock-stage-1', word: 'night',    meaning: '밤',        example: null, orderIndex: 4  },
    { wordId: 'w5',  stageId: 'mock-stage-1', word: 'door',     meaning: '문',        example: null, orderIndex: 5  },
    { wordId: 'w6',  stageId: 'mock-stage-1', word: 'clock',    meaning: '시계',      example: null, orderIndex: 6  },
    { wordId: 'w7',  stageId: 'mock-stage-1', word: 'watch',    meaning: '손목시계',  example: null, orderIndex: 7  },
    { wordId: 'w8',  stageId: 'mock-stage-1', word: 'subway',   meaning: '지하철',    example: null, orderIndex: 8  },
    { wordId: 'w9',  stageId: 'mock-stage-1', word: 'paper',    meaning: '종이',      example: null, orderIndex: 9  },
    { wordId: 'w10', stageId: 'mock-stage-1', word: 'ruler',    meaning: '자',        example: null, orderIndex: 10 },
  ],
  createdAt: '2025-01-01T00:00:00.000Z',
  updatedAt: '2025-01-01T00:00:00.000Z',
};
