-- Add updated_at column to stages table (was missing from initial schema)
ALTER TABLE stages ADD COLUMN updated_at DATETIME(6) DEFAULT NULL AFTER created_at;
UPDATE stages SET updated_at = created_at WHERE updated_at IS NULL;
