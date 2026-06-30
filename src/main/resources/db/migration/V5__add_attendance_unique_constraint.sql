-- V5: deduplicate attendances and add UNIQUE constraint
-- Production-safe: removes duplicate (user_id, project_id, date) rows,
-- keeping the latest check-in per group, then adds the constraint.

DO $$
BEGIN
    -- 1. Remove duplicate attendance rows, keeping the latest (by created_at, then by id)
    DELETE FROM attendances a
    WHERE a.id IN (
        SELECT id FROM (
            SELECT id,
                   ROW_NUMBER() OVER (
                       PARTITION BY user_id, project_id, date
                       ORDER BY created_at DESC, check_in DESC, id DESC
                   ) AS rn
            FROM attendances
        ) ranked
        WHERE ranked.rn > 1
    );

    -- 2. Now safe to add the unique constraint
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uq_attendance_user_project_date'
          AND conrelid = 'attendances'::regclass
    ) THEN
        ALTER TABLE attendances
            ADD CONSTRAINT uq_attendance_user_project_date
            UNIQUE (user_id, project_id, date);
    END IF;
END;
$$;
