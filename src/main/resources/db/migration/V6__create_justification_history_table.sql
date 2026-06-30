CREATE TABLE justification_history (
    id              UUID            PRIMARY KEY,
    justification_id UUID           NOT NULL,
    previous_status VARCHAR(30),
    new_status      VARCHAR(30)     NOT NULL,
    comment         TEXT,
    changed_by      UUID,
    changed_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_jh_justification_id ON justification_history(justification_id);
