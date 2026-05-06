-- TOKKI database operation queries.
-- These queries are intentionally metadata/admin oriented and avoid exposing secrets.
-- Run against the backend database configured by SPRING_DATASOURCE_URL.

-- 1. Confirm the selected database and MySQL version.
SELECT DATABASE() AS current_database, VERSION() AS mysql_version;

-- 2. List application tables with approximate row counts.
SELECT
    table_name,
    engine,
    table_rows AS approximate_rows,
    table_collation
FROM information_schema.tables
WHERE table_schema = DATABASE()
ORDER BY table_name;

-- 3. Inspect columns for all TOKKI tables.
SELECT
    table_name,
    ordinal_position,
    column_name,
    column_type,
    is_nullable,
    column_key,
    column_default,
    extra
FROM information_schema.columns
WHERE table_schema = DATABASE()
ORDER BY table_name, ordinal_position;

-- 4. Inspect foreign key relationships.
SELECT
    table_name,
    column_name,
    referenced_table_name,
    referenced_column_name,
    constraint_name
FROM information_schema.key_column_usage
WHERE table_schema = DATABASE()
  AND referenced_table_name IS NOT NULL
ORDER BY table_name, column_name;

-- 5. Check users by role without exposing OAuth identifiers beyond counts.
SELECT role, COUNT(*) AS user_count
FROM users
GROUP BY role
ORDER BY role;

-- 6. Promote a known user to admin.
-- Replace the uid value after confirming the target user.
UPDATE users
SET role = 'admin'
WHERE uid = '<target-user-uid>';

-- 7. Demote a known admin to a normal user.
-- Replace the uid value after confirming the target user.
UPDATE users
SET role = 'user'
WHERE uid = '<target-user-uid>';

-- 8. Confirm stage and word loading coverage.
SELECT
    s.id AS stage_id,
    s.level,
    s.title,
    COUNT(w.id) AS word_count
FROM stages s
LEFT JOIN words w ON w.stage_id = s.id
GROUP BY s.id, s.level, s.title
ORDER BY s.level, s.id;

-- 9. Find quiz sessions with answer counts.
SELECT
    qs.id AS session_id,
    qs.uid,
    qs.stage_id,
    qs.score,
    qs.total_questions,
    COUNT(qa.id) AS answer_count,
    qs.started_at,
    qs.completed_at
FROM quiz_sessions qs
LEFT JOIN quiz_answers qa ON qa.session_id = qs.id
GROUP BY qs.id, qs.uid, qs.stage_id, qs.score, qs.total_questions, qs.started_at, qs.completed_at
ORDER BY qs.started_at DESC;

-- 10. Inspect current table DDL when drift is suspected.
-- Run one table at a time:
-- SHOW CREATE TABLE users;
-- SHOW CREATE TABLE stages;
-- SHOW CREATE TABLE words;
-- SHOW CREATE TABLE user_progress;
-- SHOW CREATE TABLE incorrect_words;
-- SHOW CREATE TABLE word_relations;
-- SHOW CREATE TABLE quiz_sessions;
-- SHOW CREATE TABLE quiz_answers;
-- SHOW CREATE TABLE rankings;
-- SHOW CREATE TABLE pvp_rooms;
-- SHOW CREATE TABLE pvp_results;
