# TOKKI Manual Test Scenarios

## Offline Queue Manual Test Scenarios

This document describes manual test scenarios for the offline progress queue functionality.

### Prerequisites
- Disable network connection in browser DevTools (Network tab -> Throttling -> Offline)
- Or use a different device without internet connection
- Clear localStorage before starting each test

### Test Scenario 1: Quiz Completion While Offline

**Steps:**
1. Open DevTools Network tab and set to "Offline"
2. Navigate to a stage and start a quiz
3. Complete all questions (submit answers)
4. Verify on result page that the status shows "오프라인 저장됨" (amber badge)
5. Re-enable network connection
6. Verify the status changes to "저장 완료" (green badge)
7. Refresh the page and check the stage is marked as completed

**Expected Result:**
- Quiz completes successfully while offline
- Progress is queued locally
- Progress syncs automatically when connection is restored
- Stage completion status persists after page refresh

### Test Scenario 2: Incorrect Word While Offline

**Steps:**
1. Open DevTools Network tab and set to "Offline"
2. Navigate to a stage and start a quiz
3. Intentionally answer some questions incorrectly
4. Complete the quiz
5. Verify result page shows offline status
6. Re-enable network connection
7. Navigate to the same stage again
8. Verify "Incorrect Note" screen appears with the words you got wrong

**Expected Result:**
- Incorrect words are recorded locally while offline
- After sync, incorrect words appear in the note

### Test Scenario 3: Multiple Offline Sessions

**Steps:**
1. Complete Stage 1 quiz while offline
2. Complete Stage 2 quiz while offline
3. Re-enable network connection
4. Verify both stages show as completed
5. Verify both stages' progress data was synced correctly

**Expected Result:**
- Multiple offline sessions are queued properly
- All sessions sync correctly when connection is restored

### Test Scenario 4: Offline Draft Resume

**Steps:**
1. Start a quiz while offline
2. Answer a few questions
3. Close the browser tab (simulate app crash)
4. Re-open the app (still offline)
5. Navigate to the same stage
6. Verify "이어서 풀기" (Resume) option is available
7. Click resume and verify your previous answers are restored
8. Complete the quiz
9. Re-enable network connection
10. Verify full quiz result is synced

**Expected Result:**
- Draft sessions are saved locally while offline
- Draft can be resumed after app restart
- Final result syncs correctly when connection is restored

### Test Scenario 5: Network Fluctuation

**Steps:**
1. Start a quiz online
2. Answer question 1
3. Go offline
3. Answer questions 2-5
4. Go back online
5. Answer questions 6-10
6. Complete the quiz
7. Verify all answers were saved correctly

**Expected Result:**
- System handles network transitions gracefully
- All answers are preserved regardless of network state

### Test Scenario 6: Queue Persistence

**Steps:**
1. Complete a quiz while offline
2. Verify offline status badge
3. Close browser completely
4. Re-open browser and navigate to TOKKI
5. Verify queue still shows "오프라인 저장됨"
6. Go online
7. Verify sync completes

**Expected Result:**
- Offline queue persists across browser sessions
- Sync happens when connection is restored

### Test Scenario 7: Sync Failure Handling

**Steps:**
1. Use DevTools to block API requests (not offline mode)
2. Complete a quiz
3. Observe the status badge
4. Stop blocking requests
5. Verify retry mechanism kicks in

**Expected Result:**
- Failed sync shows appropriate error status
- System retries when connection is available

### Test Scenario 8: Large Batch Offline

**Steps:**
1. Go offline
2. Complete 5 different stages in succession
3. Go online
4. Monitor that all 5 stages sync correctly
5. Check that each stage shows correct completion

**Expected Result:**
- Multiple offline operations queue correctly
- Batch sync processes all queued operations

## Checklist for Each Test

- [ ] Initial state verified (no offline queue)
- [ ] Operation performed offline
- [ ] Offline indicator appeared
- [ ] Data persisted locally
- [ ] Connection restored
- [ ] Sync indicator appeared
- [ ] Data verified on server
- [ ] UI reflects correct state after sync

## Notes

- Tests should be performed on both desktop and mobile browsers
- Check both Chrome and Safari/Edge for compatibility
- Verify behavior on both slow 3G and completely offline conditions
- Test with both login and logout states where applicable
