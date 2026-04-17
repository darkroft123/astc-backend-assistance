-- =============================================================================
-- SMMS Assistance Service - Domain Tables
-- =============================================================================
-- NOTE: This migration creates ONLY tables owned by the Assistance domain.
-- Tables like users, projects, project_users are managed by other services.
-- References to external entities use UUID columns without foreign keys.
-- =============================================================================

CREATE TABLE attendances (
    id              UUID            PRIMARY KEY,
    user_id         UUID            NOT NULL,
    project_id      UUID,
    date            DATE            NOT NULL,
    check_in        TIME,
    check_out       TIME,
    status          VARCHAR(30)     NOT NULL,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    photo_url       VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE TABLE absences (
    id              UUID            PRIMARY KEY,
    user_id         UUID            NOT NULL,
    project_id      UUID,
    date            DATE            NOT NULL,
    type            VARCHAR(30)     NOT NULL,
    justified       BOOLEAN         DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE TABLE justifications (
    id              UUID            PRIMARY KEY,
    absence_id      UUID            NOT NULL REFERENCES absences(id),
    user_id         UUID            NOT NULL,
    description     TEXT,
    document_url    VARCHAR(500),
    status          VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    comment         TEXT,
    submitted_at    TIMESTAMP,
    reviewed_at     TIMESTAMP,
    reviewed_by     UUID,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

-- Indexes
CREATE INDEX idx_attendances_user       ON attendances(user_id);
CREATE INDEX idx_attendances_project    ON attendances(project_id);
CREATE INDEX idx_attendances_date       ON attendances(date);
CREATE INDEX idx_absences_user          ON absences(user_id);
CREATE INDEX idx_absences_date          ON absences(date);
CREATE INDEX idx_justifications_user    ON justifications(user_id);
CREATE INDEX idx_justifications_status  ON justifications(status);
CREATE INDEX idx_justifications_absence ON justifications(absence_id);
