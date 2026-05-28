-- Migrate existing TOKKI words data from the old backend shape to the frontend-aligned shape.
-- Old shape:
--   korean  = Korean meaning
--   meaning = English word
-- New shape:
--   word       = English word
--   meaning    = Korean meaning
--   order_index = display/quiz order within a stage

ALTER TABLE words
    RENAME COLUMN meaning TO word;

ALTER TABLE words
    RENAME COLUMN korean TO meaning;

ALTER TABLE words
    ADD COLUMN order_index INT NULL AFTER image_url;

UPDATE words w
JOIN (
    SELECT
        id,
        ROW_NUMBER() OVER (PARTITION BY stage_id ORDER BY id) AS generated_order_index
    FROM words
) ordered_words ON ordered_words.id = w.id
SET w.order_index = ordered_words.generated_order_index;

ALTER TABLE words
    MODIFY word VARCHAR(100) NOT NULL,
    MODIFY meaning VARCHAR(200) NOT NULL,
    MODIFY order_index INT NOT NULL;

-- Keep migrated table column order aligned with backend/src/main/resources/schema.sql.
ALTER TABLE words
    MODIFY word VARCHAR(100) NOT NULL AFTER stage_id,
    MODIFY meaning VARCHAR(200) NOT NULL AFTER word;
