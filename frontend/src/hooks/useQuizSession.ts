import { useState, useCallback, useRef } from 'react';
import { useAuth } from './useAuth';
import { QuizSessionService } from '../services/QuizSessionService';
import { OfflineProgressQueue, type ProgressSyncStatus } from '../services/OfflineProgressQueue';
import type { Word, QuizMode } from '../types';

export interface QuestionResult {
  word: Word;
  userAnswer: string;
  correctAnswer: string;
  isCorrect: boolean;
}

export interface QuizSessionState {
  mode: QuizMode;
  currentIndex: number;
  answer: string;
  submitted: boolean;
  results: QuestionResult[];
  syncStatus: ProgressSyncStatus;
}

export interface UseQuizSessionOptions {
  words: Word[];
  stageId: number;
  mode: QuizMode;
  onCompleted?: (finalResults: QuestionResult[]) => void;
}

export function useQuizSession({
  words,
  stageId,
  mode: initialMode,
  onCompleted,
}: UseQuizSessionOptions) {
  const { user } = useAuth();
  const savingRef = useRef(false);

  const [mode, setMode] = useState<QuizMode>(initialMode);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answer, setAnswer] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [results, setResults] = useState<QuestionResult[]>([]);
  const [syncStatus] = useState<ProgressSyncStatus>(
    OfflineProgressQueue.hasPending() ? 'queued' : 'idle'
  );

  const getPrompt = useCallback((word: Word) => {
    return mode === 'KtoE' ? word.meaning : word.word;
  }, [mode]);

  const getCorrect = useCallback((word: Word) => {
    return mode === 'KtoE' ? word.word : word.meaning;
  }, [mode]);

  const saveDraft = useCallback(async (newResults: QuestionResult[]) => {
    if (!user || savingRef.current) return;

    savingRef.current = true;
    try {
      await QuizSessionService.upsertDraftSession({
        stageId,
        mode,
        currentIndex: currentIndex + 1,
        score: newResults.filter(r => r.isCorrect).length,
        totalQuestions: words.length,
        answers: newResults.map(r => ({
          wordId: r.word.wordId,
          userAnswer: r.userAnswer,
          correct: r.isCorrect
        }))
      });
    } catch (err) {
      console.error('Failed to save draft:', err);
    } finally {
      savingRef.current = false;
    }
  }, [user, stageId, mode, currentIndex, words.length]);

  const submitAnswer = useCallback(async (userAnswer: string) => {
    const word = words[currentIndex];
    if (!word || submitted) return { success: false };

    const correctAnswer = getCorrect(word);
    const isCorrect = userAnswer.trim().toLowerCase() === correctAnswer.trim().toLowerCase();
    const newResult: QuestionResult = { word, userAnswer, correctAnswer, isCorrect };

    setResults(prev => [...prev, newResult]);
    setSubmitted(true);

    await saveDraft([...results, newResult]);

    return { success: true, isCorrect };
  }, [words, currentIndex, submitted, getCorrect, results, saveDraft]);

  const skipQuestion = useCallback(async () => {
    const word = words[currentIndex];
    if (!word || submitted) return { success: false };

    const correctAnswer = getCorrect(word);
    const newResult: QuestionResult = { word, userAnswer: '', correctAnswer, isCorrect: false };

    setResults(prev => [...prev, newResult]);
    setSubmitted(true);

    await saveDraft([...results, newResult]);

    return { success: true };
  }, [words, currentIndex, submitted, getCorrect, results, saveDraft]);

  const nextQuestion = useCallback(() => {
    if (currentIndex < words.length - 1) {
      setCurrentIndex(i => i + 1);
      setAnswer('');
      setSubmitted(false);
    } else {
      onCompleted?.(results);
    }
  }, [currentIndex, words.length, results, onCompleted]);

  const resetQuiz = useCallback(() => {
    setCurrentIndex(0);
    setResults([]);
    setAnswer('');
    setSubmitted(false);
  }, []);

  return {
    state: {
      mode,
      currentIndex,
      answer,
      submitted,
      results,
      syncStatus,
      words,
      currentWord: words[currentIndex] || null,
      isLastQuestion: currentIndex === words.length - 1,
      progressValue: currentIndex + (submitted ? 1 : 0),
    },
    actions: {
      setMode,
      setAnswer,
      submitAnswer,
      skipQuestion,
      nextQuestion,
      resetQuiz,
      getPrompt,
      getCorrect,
    },
  };
}
