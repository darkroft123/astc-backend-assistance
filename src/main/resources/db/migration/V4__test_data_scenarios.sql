-- =============================================================================
-- Test Data: All Justification Scenarios
-- =============================================================================

-- =============================================================================
-- USER IDS (from auth/backoffice service):
-- 00000000-0000-0000-0000-000000000001 → Admin
-- 00000000-0000-0000-0000-000000000002 → Team Member (Carlos Mamani)
-- 00000000-0000-0000-0000-000000000003 → Project Manager (Maria Torres)
-- =============================================================================

-- =============================================================================
-- 1. ATTENDANCE RECORDS (variety of statuses)
-- =============================================================================
INSERT INTO attendances (id, user_id, project_id, date, check_in, check_out, status, latitude, longitude, created_at, updated_at)
VALUES
-- User 2: Present on time
('a1000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 5, '08:00:00', '17:00:00', 'PRESENTE', -12.0464, -77.0428, now(), now()),
-- User 2: Late arrival
('a1000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 4, '09:30:00', '17:15:00', 'TARDE', -12.0464, -77.0428, now(), now()),
-- User 2: Present
('a1000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 3, '07:55:00', '17:00:00', 'PRESENTE', -12.0464, -77.0428, now(), now());

-- =============================================================================
-- 2. ABSENCE RECORDS
-- =============================================================================
INSERT INTO absences (id, user_id, project_id, date, type, justified, created_at, updated_at)
VALUES
-- User 2: Absence WITHOUT justification (for Scenario A)
('b1000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 2, 'FALTA', FALSE, now(), now()),
-- User 2: Absence WITH pending justification (for Scenario B)
('b1000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 1, 'FALTA', TRUE, now(), now()),
-- User 2: Absence WITH approved justification
('b1000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 7, 'FALTA', TRUE, now(), now()),
-- User 2: Absence WITH rejected justification
('b1000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 14, 'FALTA', FALSE, now(), now()),
-- User 2: Late arrival that generates an absence record
('b1000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', CURRENT_DATE - 4, 'TARDE', FALSE, now(), now());

-- =============================================================================
-- 3. JUSTIFICATION RECORDS
-- =============================================================================
INSERT INTO justifications (id, absence_id, user_id, description, document_url, status, comment, submitted_at, reviewed_at, reviewed_by, created_at, updated_at)
VALUES
-- PENDING justification (for Scenario A: approve)
('c1000000-0000-0000-0000-000000000001', 'b1000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002',
 'Asistí a una cita médica programada con el especialista. Adjunto el certificado médico.',
 NULL,
 'PENDING', NULL,
 now() - interval '2 days', NULL, NULL,
 now(), now()),

-- APPROVED justification (for display in TM view)
('c1000000-0000-0000-0000-000000000002', 'b1000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002',
 'Problemas personales que requirieron mi atención todo el día.',
 NULL,
 'APPROVED', 'Justificación aceptada. Por favor coordina con tu supervisor los días pendientes.',
 now() - interval '10 days', now() - interval '8 days',
 '00000000-0000-0000-0000-000000000003',
 now(), now()),

-- REJECTED justification (for display in TM view)
('c1000000-0000-0000-0000-000000000003', 'b1000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002',
 'No pude asistir porque tuve un problema con el transporte público.',
 NULL,
 'REJECTED', 'El motivo no es suficiente para justificar una falta completa. Se requiere documento de respaldo.',
 now() - interval '20 days', now() - interval '18 days',
 '00000000-0000-0000-0000-000000000003',
 now(), now());
