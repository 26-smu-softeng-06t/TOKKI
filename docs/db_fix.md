# DB 마이그레이션 보류 항목

## 배경

`Stage` 엔티티에 `updated_at` 컬럼을 추가해야 하지만, DB 접근 권한이 없어 적용이 보류됩니다.
`application.yml`의 `ddl-auto: validate` 설정으로 인해 엔티티와 실제 DB 스키마가 일치하지 않으면 앱이 기동되지 않습니다.

---

## 적용이 필요한 마이그레이션

### 1. DB에 직접 실행할 SQL

```sql
-- stages 테이블에 updated_at 컬럼 추가
ALTER TABLE stages ADD COLUMN updated_at DATETIME(6) DEFAULT NULL AFTER created_at;

-- 기존 rows는 created_at 값으로 초기화
UPDATE stages SET updated_at = created_at WHERE updated_at IS NULL;
```

### 2. `Stage.java` 엔티티에 추가할 코드

`backend/src/main/java/com/tokki/domain/Stage.java`

```java
@Column(name = "updated_at")
private LocalDateTime updatedAt;
```

`@PrePersist`에 `updatedAt = LocalDateTime.now();` 추가:

```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();   // 추가
    level = stageNumber;
    ...
}
```

`@PreUpdate` 메서드 추가:

```java
@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

### 3. `StageResponse.java`에 추가할 코드

`backend/src/main/java/com/tokki/dto/response/StageResponse.java`

`updatedAt` 필드 선언 추가:
```java
private LocalDateTime updatedAt;
```

`from()` 팩토리 메서드에 매핑 추가:
```java
.updatedAt(stage.getUpdatedAt())
```

### 4. `schema.sql` 변경

`backend/src/main/resources/schema.sql`의 `stages` 테이블 정의에 컬럼 추가:

```sql
updated_at DATETIME(6) DEFAULT NULL,
```

---

## 적용 순서

1. DB 접근 권한 확보
2. 위 SQL을 운영/개발 DB에 직접 실행
3. `Stage.java`, `StageResponse.java`, `schema.sql` 코드 변경 적용
4. 앱 재기동 후 `ddl-auto: validate` 통과 확인

---

## 관련 이슈

- AGENTS.md Domain Model: `tokki_vocab.Stage`에 `updatedAt` 필드가 명세에 포함됨
- `StageResponse`는 현재 `updatedAt`을 반환하지 않음 (프론트 `Stage` 타입의 `updatedAt` 필드가 빈 문자열로 채워짐)
