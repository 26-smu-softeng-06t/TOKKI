import { useEffect, useRef, useState, useCallback } from 'react';
import type { ProgressMessage, BattleResultMessage } from '../types';

interface UsePvpOptions {
  roomId: number | null;
  onProgress?: (message: ProgressMessage) => void;
  onBattleComplete?: (message: BattleResultMessage) => void;
  onRoomState?: (playerCount: number) => void;
  onError?: (error: Error) => void;
}

interface HandlersRef {
  onProgress?: (message: ProgressMessage) => void;
  onBattleComplete?: (message: BattleResultMessage) => void;
  onRoomState?: (playerCount: number) => void;
  onError?: (error: Error) => void;
}

export function usePvp({ roomId, onProgress, onBattleComplete, onRoomState, onError }: UsePvpOptions) {
  const wsRef = useRef<WebSocket | null>(null);
  const [connected, setConnected] = useState(false);
  const reconnectTimeoutRef = useRef<ReturnType<typeof setTimeout> | undefined>(undefined);
  const handlersRef = useRef<HandlersRef>({ onProgress, onBattleComplete, onRoomState });

  // Keep handlers ref up-to-date
  useEffect(() => {
    handlersRef.current = { onProgress, onBattleComplete, onRoomState, onError };
  }, [onProgress, onBattleComplete, onRoomState, onError]);

  const disconnect = useCallback(() => {
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
    }
    if (wsRef.current) {
      wsRef.current.close();
      wsRef.current = null;
    }
    setConnected(false);
  }, []);

  const sendProgress = useCallback((progress: ProgressMessage) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({
        type: 'PROGRESS',
        roomId,
        ...progress,
      }));
    }
  }, [roomId]);

  const sendBattleComplete = useCallback((result: BattleResultMessage) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({
        type: 'BATTLE_COMPLETE',
        roomId,
        ...result,
      }));
    }
  }, [roomId]);

  useEffect(() => {
    // Only connect when roomId is available
    if (!roomId) return;

    const wsUrl = `${import.meta.env.VITE_WS_URL || 'ws://localhost:8080'}/ws/pvp?roomId=${roomId}`;
    const ws = new WebSocket(wsUrl);

    ws.onopen = () => {
      setConnected(true);
      ws.send(JSON.stringify({ type: 'JOIN_ROOM', roomId }));
    };

    ws.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        const handlers = handlersRef.current;
        switch (message.type) {
          case 'PROGRESS':
            handlers.onProgress?.(message);
            break;
          case 'BATTLE_COMPLETE':
            handlers.onBattleComplete?.(message);
            break;
          case 'ROOM_STATE':
            handlers.onRoomState?.(message.playerCount);
            break;
        }
      } catch (e) {
        console.error('Failed to parse WebSocket message:', e);
      }
    };

    ws.onclose = () => {
      setConnected(false);
    };

    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
      handlersRef.current.onError?.(new Error('WebSocket connection failed'));
    };

    wsRef.current = ws;

    // Cleanup function
    return () => {
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
      if (wsRef.current) {
        wsRef.current.close();
        wsRef.current = null;
      }
      setConnected(false);
    };
  }, [roomId]); // Only depend on roomId

  return {
    connected,
    sendProgress,
    sendBattleComplete,
    disconnect,
  };
}
