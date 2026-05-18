-- One-shot migration for existing TOKKI databases before running the issue #31 model.
-- Apply once, then run db/seed/issue-31-word-seed.sql for development seed data.

ALTER TABLE stages
    ADD COLUMN difficulty ENUM('easy', 'medium', 'hard') NOT NULL DEFAULT 'easy',
    ADD COLUMN stage_number INT NULL;

UPDATE stages
SET difficulty = CASE
        WHEN LOWER(title) LIKE 'medium%' THEN 'medium'
        WHEN LOWER(title) LIKE 'hard%' OR LOWER(title) LIKE 'high%' THEN 'hard'
        ELSE 'easy'
    END,
    stage_number = COALESCE(
        CAST(REGEXP_SUBSTR(title, '[0-9]+$') AS UNSIGNED),
        CASE
            WHEN level BETWEEN 1 AND 10 THEN level
            ELSE 1
        END
    );

UPDATE stages
SET level = stage_number;

ALTER TABLE stages
    MODIFY COLUMN stage_number INT NOT NULL,
    ADD UNIQUE KEY uq_stages_difficulty_stage_number (difficulty, stage_number),
    ADD CONSTRAINT chk_stages_stage_number CHECK (stage_number BETWEEN 1 AND 10),
    ADD CONSTRAINT chk_stages_level CHECK (level BETWEEN 1 AND 10);
