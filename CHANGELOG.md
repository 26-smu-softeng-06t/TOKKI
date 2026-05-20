# Changelog

All notable changes to TOKKI will be documented in this file.

## [1.0.0-rc.1] - 2026-05-20

### Added
- **Phase 1 MVP Core Features**
  - SSO 로그인 (Google OAuth2, Firebase Authentication)
  - 난이도 선택 (하/중/상, 10단계)
  - 단어 카드 학습 (영단어, 뜻, 예문)
  - 주관식 퀴즈 (한→영, 영→한)
  - 퀴즈 결과 확인 및 오답노트 자동 기록
  - 관리자 단어 CRUD
  - 모바일 반응형 UI

- **Phase 2 Features**
  - 엑셀 일괄 업로드 (.xlsx)
  - 랭킹 조회 (오답 빈도 TOP 10)
  - 퀴즈 이어풀기 (중단 후 재개)
  - TTS/연관어/숙어 확장 학습

- **Phase 3 Features**
  - PvP 실시간 대결 (WebSocket)
  - 오프라인 임시 저장/동기화

### Technical
- **Backend**: Spring Boot 3.x, Java 17, MySQL
- **Frontend**: React 18, TypeScript, Tailwind CSS, Vite
- **Authentication**: Firebase Authentication, JWT
- **Real-time**: WebSocket (PvP)

### Documentation
- AGENTS.md (AI Agent Constitution)
- API 명세서
- ERD
- UI 명세서
- 요구사항명세서

### Known Issues
- 번들 크기 500KB 초과 (코드 스플릿 필요)
- 자동화 테스트 미도입

### Next Steps
- 배포 환경 구축
- 자동화 테스트 도입
- 성능 최적화
