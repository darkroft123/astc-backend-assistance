-- V7: Seed realistic test data for all attendance workflows
-- NOTE: user_id and project_id are fixed test UUIDs (no FK constraints)
-- Replace with actual IDs after system initialization

DO $$
DECLARE
    today date := CURRENT_DATE;
    -- Replace these with real user/project UUIDs after initial setup
    user1 UUID := '00000000-0000-0000-0000-000000000001';
    user2 UUID := '00000000-0000-0000-0000-000000000002';
    pm1   UUID := '00000000-0000-0000-0000-000000000010';
    proj1 UUID := '00000000-0000-0000-0000-000000000100';
    proj2 UUID := '00000000-0000-0000-0000-000000000200';
BEGIN

    -- =========================================
    -- ATTENDANCES (ON_TIME + LATE)
    -- =========================================
    INSERT INTO attendances (id, user_id, project_id, date, check_in, check_out, status, latitude, longitude, photo_url, created_at, updated_at)
    VALUES
    (gen_random_uuid(), user1, proj1, today,     '07:45:00', null, 'ON_TIME', -12.0464, -77.0428, null, now(), now()),
    (gen_random_uuid(), user1, proj1, today - 1, '08:25:00', null, 'LATE',    -12.0464, -77.0428, null, now(), now()),
    (gen_random_uuid(), user1, proj1, today - 2, '07:50:00', null, 'ON_TIME', -12.0464, -77.0428, null, now(), now()),
    (gen_random_uuid(), user1, proj1, today - 7, '08:30:00', null, 'LATE',    -12.0464, -77.0428, null, now(), now()),
    (gen_random_uuid(), user2, proj2, today,     '07:30:00', null, 'ON_TIME', -12.0464, -77.0428, null, now(), now()),
    (gen_random_uuid(), user2, proj2, today - 1, '08:45:00', null, 'LATE',    -12.0464, -77.0428, null, now(), now())
    ON CONFLICT (user_id, project_id, date) DO NOTHING;

    RAISE NOTICE '[Seed] Inserted 6 attendances';

    -- =========================================
    -- ABSENCES (UNJUSTIFIED)
    -- =========================================
    INSERT INTO absences (id, user_id, project_id, date, type, justified, created_at, updated_at)
    VALUES
    (gen_random_uuid(), user1, proj1, today - 3, 'UNJUSTIFIED', false, now(), now()),
    (gen_random_uuid(), user1, proj1, today - 5, 'UNJUSTIFIED', false, now(), now()),
    (gen_random_uuid(), user1, proj1, today - 6, 'UNJUSTIFIED', false, now(), now()),
    (gen_random_uuid(), user2, proj2, today - 3, 'UNJUSTIFIED', false, now(), now()),
    (gen_random_uuid(), user2, proj2, today - 4, 'UNJUSTIFIED', false, now(), now());

    RAISE NOTICE '[Seed] Inserted 5 absences';

    -- =========================================
    -- JUSTIFICATIONS + HISTORY (all statuses)
    -- Uses INSERT ... RETURNING for chaining
    -- =========================================

    -- 1. PENDING (not yet submitted)
    WITH ins AS (
        INSERT INTO justifications (id, absence_id, user_id, description, document_url, status, comment, submitted_at, reviewed_at, reviewed_by, created_at, updated_at)
        VALUES (gen_random_uuid(), (SELECT id FROM absences WHERE user_id = user1 AND date = today - 3 LIMIT 1), user1,
                'Justificación de prueba - PENDIENTE (sin enviar)', null, 'PENDING', null, null, null, null, now(), now())
        RETURNING id
    )
    INSERT INTO justification_history (id, justification_id, previous_status, new_status, comment, changed_by, changed_at)
    SELECT gen_random_uuid(), ins.id, null, 'PENDING', 'Justificación creada', user1, now()
    FROM ins;

    -- 2. SUBMITTED
    WITH ins AS (
        INSERT INTO justifications (id, absence_id, user_id, description, document_url, status, comment, submitted_at, reviewed_at, reviewed_by, created_at, updated_at)
        VALUES (gen_random_uuid(), (SELECT id FROM absences WHERE user_id = user1 AND date = today - 5 LIMIT 1), user1,
                'Justificación por motivos de salud', null, 'SUBMITTED', null, now() - interval '2 hours', null, null, now(), now())
        RETURNING id
    )
    INSERT INTO justification_history (id, justification_id, previous_status, new_status, comment, changed_by, changed_at)
    SELECT gen_random_uuid(), ins.id, v.s, v.n, v.c, v.u, v.t
    FROM ins, (VALUES
        (null::varchar, 'PENDING'::varchar, 'Justificación creada'::text, user1, now() - interval '4 hours'),
        ('PENDING', 'SUBMITTED', 'Enviada para revisión', user1, now() - interval '2 hours')
    ) AS v(s, n, c, u, t);

    -- 3. OBSERVATION
    WITH ins AS (
        INSERT INTO justifications (id, absence_id, user_id, description, document_url, status, comment, submitted_at, reviewed_at, reviewed_by, created_at, updated_at)
        VALUES (gen_random_uuid(), (SELECT id FROM absences WHERE user_id = user1 AND date = today - 6 LIMIT 1), user1,
                'Justificación por emergencia familiar', null, 'OBSERVATION',
                'Por favor adjuntar documento que acredite la emergencia',
                now() - interval '1 day', now() - interval '3 hours', pm1, now(), now())
        RETURNING id
    )
    INSERT INTO justification_history (id, justification_id, previous_status, new_status, comment, changed_by, changed_at)
    SELECT gen_random_uuid(), ins.id, v.s, v.n, v.c, v.u, v.t
    FROM ins, (VALUES
        (null::varchar, 'PENDING'::varchar, 'Justificación creada'::text, user1, now() - interval '2 days'),
        ('PENDING', 'SUBMITTED', 'Enviada para revisión', user1, now() - interval '1 day'),
        ('SUBMITTED', 'OBSERVATION', 'Solicitud de observación: Por favor adjuntar documento que acredite la emergencia', pm1, now() - interval '3 hours')
    ) AS v(s, n, c, u, t);

    -- 4. APPROVED
    WITH ins AS (
        INSERT INTO justifications (id, absence_id, user_id, description, document_url, status, comment, submitted_at, reviewed_at, reviewed_by, created_at, updated_at)
        VALUES (gen_random_uuid(), (SELECT id FROM absences WHERE user_id = user2 AND date = today - 3 LIMIT 1), user2,
                'Cita médica con certificado adjunto', '/documents/certificado.pdf', 'APPROVED',
                'Documento verificado. Falta justificada.',
                now() - interval '2 days', now() - interval '1 day', pm1, now(), now())
        RETURNING id
    )
    INSERT INTO justification_history (id, justification_id, previous_status, new_status, comment, changed_by, changed_at)
    SELECT gen_random_uuid(), ins.id, v.s, v.n, v.c, v.u, v.t
    FROM ins, (VALUES
        (null::varchar, 'PENDING'::varchar, 'Justificación creada'::text, user2, now() - interval '3 days'),
        ('PENDING', 'SUBMITTED', 'Enviada para revisión', user2, now() - interval '2 days'),
        ('SUBMITTED', 'APPROVED', 'Documento verificado. Falta justificada.', pm1, now() - interval '1 day')
    ) AS v(s, n, c, u, t);

    -- 5. REJECTED
    WITH ins AS (
        INSERT INTO justifications (id, absence_id, user_id, description, document_url, status, comment, submitted_at, reviewed_at, reviewed_by, created_at, updated_at)
        VALUES (gen_random_uuid(), (SELECT id FROM absences WHERE user_id = user2 AND date = today - 4 LIMIT 1), user2,
                'Problemas de transporte', null, 'REJECTED',
                'Motivo no válido según reglamento interno',
                now() - interval '4 days', now() - interval '3 days', pm1, now(), now())
        RETURNING id
    )
    INSERT INTO justification_history (id, justification_id, previous_status, new_status, comment, changed_by, changed_at)
    SELECT gen_random_uuid(), ins.id, v.s, v.n, v.c, v.u, v.t
    FROM ins, (VALUES
        (null::varchar, 'PENDING'::varchar, 'Justificación creada'::text, user2, now() - interval '5 days'),
        ('PENDING', 'SUBMITTED', 'Enviada para revisión', user2, now() - interval '4 days'),
        ('SUBMITTED', 'REJECTED', 'Motivo no válido según reglamento interno', pm1, now() - interval '3 days')
    ) AS v(s, n, c, u, t);

    RAISE NOTICE '[Seed] Inserted 5 justifications with history';

    RAISE NOTICE '[Seed] Seed data complete: 6 attendances, 5 absences, 5 justifications';
END;
$$;
