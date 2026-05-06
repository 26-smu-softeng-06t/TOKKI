CREATE TABLE IF NOT EXISTS users (
    created_at DATETIME(6) NOT NULL,
    nickname   VARCHAR(50) NOT NULL,
    email      VARCHAR(100) DEFAULT NULL,
    uid        VARCHAR(128) NOT NULL,
    role       ENUM('admin', 'user') NOT NULL,
    PRIMARY KEY (uid)
);

CREATE TABLE IF NOT EXISTS stages (
    level       INT NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    id          BIGINT NOT NULL AUTO_INCREMENT,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS words (
    created_at DATETIME(6) NOT NULL,
    id         BIGINT NOT NULL AUTO_INCREMENT,
    stage_id   BIGINT NOT NULL,
    korean     VARCHAR(100) NOT NULL,
    meaning    VARCHAR(200) NOT NULL,
    example    VARCHAR(500) DEFAULT NULL,
    image_url  VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_words_stage (stage_id),
    CONSTRAINT fk_words_stage FOREIGN KEY (stage_id) REFERENCES stages (id)
);

CREATE TABLE IF NOT EXISTS user_progress (
    completed    BIT(1) NOT NULL,
    completed_at DATETIME(6) DEFAULT NULL,
    id           BIGINT NOT NULL AUTO_INCREMENT,
    stage_id     BIGINT NOT NULL,
    uid          VARCHAR(128) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_progress_user_stage (uid, stage_id),
    KEY fk_user_progress_stage (stage_id),
    CONSTRAINT fk_user_progress_stage FOREIGN KEY (stage_id) REFERENCES stages (id),
    CONSTRAINT fk_user_progress_user FOREIGN KEY (uid) REFERENCES users (uid)
);

CREATE TABLE IF NOT EXISTS incorrect_words (
    count             INT NOT NULL,
    id                BIGINT NOT NULL AUTO_INCREMENT,
    last_incorrect_at DATETIME(6) DEFAULT NULL,
    word_id           BIGINT NOT NULL,
    uid               VARCHAR(128) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_incorrect_words_user_word (uid, word_id),
    KEY fk_incorrect_words_word (word_id),
    CONSTRAINT fk_incorrect_words_user FOREIGN KEY (uid) REFERENCES users (uid),
    CONSTRAINT fk_incorrect_words_word FOREIGN KEY (word_id) REFERENCES words (id)
);

CREATE TABLE IF NOT EXISTS word_relations (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    related_word_id BIGINT NOT NULL,
    word_id         BIGINT NOT NULL,
    relation_type   VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    KEY fk_word_relations_related_word (related_word_id),
    KEY fk_word_relations_word (word_id),
    CONSTRAINT fk_word_relations_related_word FOREIGN KEY (related_word_id) REFERENCES words (id),
    CONSTRAINT fk_word_relations_word FOREIGN KEY (word_id) REFERENCES words (id)
);

CREATE TABLE IF NOT EXISTS quiz_sessions (
    score           INT NOT NULL,
    total_questions INT NOT NULL,
    completed_at    DATETIME(6) DEFAULT NULL,
    id              BIGINT NOT NULL AUTO_INCREMENT,
    stage_id        BIGINT NOT NULL,
    started_at      DATETIME(6) NOT NULL,
    uid             VARCHAR(128) NOT NULL,
    PRIMARY KEY (id),
    KEY fk_quiz_sessions_stage (stage_id),
    KEY fk_quiz_sessions_user (uid),
    CONSTRAINT fk_quiz_sessions_stage FOREIGN KEY (stage_id) REFERENCES stages (id),
    CONSTRAINT fk_quiz_sessions_user FOREIGN KEY (uid) REFERENCES users (uid)
);

CREATE TABLE IF NOT EXISTS quiz_answers (
    correct     BIT(1) NOT NULL,
    answered_at DATETIME(6) NOT NULL,
    id          BIGINT NOT NULL AUTO_INCREMENT,
    session_id  BIGINT NOT NULL,
    word_id     BIGINT NOT NULL,
    user_answer VARCHAR(200) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_quiz_answers_session (session_id),
    KEY fk_quiz_answers_word (word_id),
    CONSTRAINT fk_quiz_answers_session FOREIGN KEY (session_id) REFERENCES quiz_sessions (id),
    CONSTRAINT fk_quiz_answers_word FOREIGN KEY (word_id) REFERENCES words (id)
);

CREATE TABLE IF NOT EXISTS rankings (
    rank_position INT NOT NULL,
    score         INT NOT NULL,
    id            BIGINT NOT NULL AUTO_INCREMENT,
    period        VARCHAR(20) NOT NULL,
    uid           VARCHAR(128) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_rankings_user_period (uid, period),
    CONSTRAINT fk_rankings_user FOREIGN KEY (uid) REFERENCES users (uid)
);

CREATE TABLE IF NOT EXISTS pvp_rooms (
    created_at DATETIME(6) NOT NULL,
    id         BIGINT NOT NULL AUTO_INCREMENT,
    stage_id   BIGINT NOT NULL,
    status     VARCHAR(20) NOT NULL,
    guest_uid  VARCHAR(128) DEFAULT NULL,
    host_uid   VARCHAR(128) NOT NULL,
    PRIMARY KEY (id),
    KEY fk_pvp_rooms_guest (guest_uid),
    KEY fk_pvp_rooms_host (host_uid),
    KEY fk_pvp_rooms_stage (stage_id),
    CONSTRAINT fk_pvp_rooms_guest FOREIGN KEY (guest_uid) REFERENCES users (uid),
    CONSTRAINT fk_pvp_rooms_host FOREIGN KEY (host_uid) REFERENCES users (uid),
    CONSTRAINT fk_pvp_rooms_stage FOREIGN KEY (stage_id) REFERENCES stages (id)
);

CREATE TABLE IF NOT EXISTS pvp_results (
    score      INT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    id         BIGINT NOT NULL AUTO_INCREMENT,
    room_id    BIGINT NOT NULL,
    result     VARCHAR(10) NOT NULL,
    uid        VARCHAR(128) NOT NULL,
    PRIMARY KEY (id),
    KEY fk_pvp_results_room (room_id),
    KEY fk_pvp_results_user (uid),
    CONSTRAINT fk_pvp_results_room FOREIGN KEY (room_id) REFERENCES pvp_rooms (id),
    CONSTRAINT fk_pvp_results_user FOREIGN KEY (uid) REFERENCES users (uid)
);
