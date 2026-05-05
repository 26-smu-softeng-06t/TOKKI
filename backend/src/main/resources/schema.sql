CREATE TABLE IF NOT EXISTS users (
    uid          VARCHAR(128) PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    role         ENUM('user', 'admin') NOT NULL DEFAULT 'user',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stages (
    stage_id     CHAR(36) PRIMARY KEY,
    difficulty   ENUM('easy', 'medium', 'hard') NOT NULL,
    stage_number TINYINT UNSIGNED NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_difficulty_stage (difficulty, stage_number)
);

CREATE TABLE IF NOT EXISTS words (
    word_id     CHAR(36)     PRIMARY KEY,
    stage_id    CHAR(36)     NOT NULL,
    word        VARCHAR(100) NOT NULL,
    meaning     VARCHAR(255) NOT NULL,
    example     TEXT         NULL,
    order_index TINYINT UNSIGNED NOT NULL,
    FOREIGN KEY (stage_id) REFERENCES stages(stage_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_progress (
    progress_id CHAR(36)         PRIMARY KEY,
    user_id     VARCHAR(128)     NOT NULL,
    stage_id    CHAR(36)         NOT NULL,
    completed   BOOLEAN          NOT NULL DEFAULT FALSE,
    last_score  TINYINT UNSIGNED NOT NULL DEFAULT 0,
    updated_at  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_stage (user_id, stage_id),
    FOREIGN KEY (user_id)  REFERENCES users(uid)   ON DELETE CASCADE,
    FOREIGN KEY (stage_id) REFERENCES stages(stage_id)
);

CREATE TABLE IF NOT EXISTS incorrect_words (
    incorrect_word_id CHAR(36) PRIMARY KEY,
    progress_id       CHAR(36) NOT NULL,
    word_id           CHAR(36) NOT NULL,
    is_resolved       BOOLEAN  NOT NULL DEFAULT FALSE,
    FOREIGN KEY (progress_id) REFERENCES user_progress(progress_id) ON DELETE CASCADE,
    FOREIGN KEY (word_id)     REFERENCES words(word_id)
);

CREATE TABLE IF NOT EXISTS word_relations (
    relation_id     CHAR(36) PRIMARY KEY,
    word_id         CHAR(36) NOT NULL,
    relation_type   ENUM('synonym','antonym','derivative','phrase') NOT NULL,
    related_word    VARCHAR(100) NOT NULL,
    related_meaning VARCHAR(255) NOT NULL,
    FOREIGN KEY (word_id) REFERENCES words(word_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_sessions (
    session_id    CHAR(36)             PRIMARY KEY,
    user_id       VARCHAR(128)         NOT NULL,
    stage_id      CHAR(36)             NOT NULL,
    mode          ENUM('EtoK','KtoE')  NOT NULL,
    current_index TINYINT UNSIGNED     NOT NULL DEFAULT 0,
    saved_at      DATETIME             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_stage_session (user_id, stage_id),
    FOREIGN KEY (user_id)  REFERENCES users(uid)   ON DELETE CASCADE,
    FOREIGN KEY (stage_id) REFERENCES stages(stage_id)
);

CREATE TABLE IF NOT EXISTS quiz_answers (
    answer_id   CHAR(36)     PRIMARY KEY,
    session_id  CHAR(36)     NOT NULL,
    word_id     CHAR(36)     NOT NULL,
    user_answer VARCHAR(255) NOT NULL,
    is_correct  BOOLEAN      NOT NULL,
    FOREIGN KEY (session_id) REFERENCES quiz_sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (word_id)    REFERENCES words(word_id)
);

CREATE TABLE IF NOT EXISTS rankings (
    ranking_id  CHAR(36)         PRIMARY KEY,
    word_id     CHAR(36)         NOT NULL,
    word        VARCHAR(100)     NOT NULL,
    meaning     VARCHAR(255)     NOT NULL,
    miss_count  INT UNSIGNED     NOT NULL,
    rank        TINYINT UNSIGNED NOT NULL,
    updated_at  DATETIME         NOT NULL,
    FOREIGN KEY (word_id) REFERENCES words(word_id)
);

CREATE TABLE IF NOT EXISTS pvp_rooms (
    room_id       CHAR(36) PRIMARY KEY,
    invite_code   CHAR(6)  NOT NULL UNIQUE,
    host_user_id  VARCHAR(128) NOT NULL,
    guest_user_id VARCHAR(128) NULL,
    stage_id      CHAR(36)     NOT NULL,
    status        ENUM('waiting','in_progress','completed') NOT NULL DEFAULT 'waiting',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (host_user_id)  REFERENCES users(uid),
    FOREIGN KEY (guest_user_id) REFERENCES users(uid),
    FOREIGN KEY (stage_id)      REFERENCES stages(stage_id)
);

CREATE TABLE IF NOT EXISTS pvp_results (
    result_id       CHAR(36)         PRIMARY KEY,
    room_id         CHAR(36)         NOT NULL,
    user_id         VARCHAR(128)     NOT NULL,
    score           TINYINT UNSIGNED NOT NULL,
    completion_time FLOAT            NOT NULL,
    is_winner       BOOLEAN          NOT NULL,
    UNIQUE KEY uq_room_user (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES pvp_rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(uid)
);
