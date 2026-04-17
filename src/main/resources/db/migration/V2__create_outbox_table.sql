-- Transactional Outbox for domain events
CREATE TABLE outbox_events (
    id              UUID            PRIMARY KEY,
    aggregate_type  VARCHAR(100)    NOT NULL,
    aggregate_id    UUID            NOT NULL,
    event_type      VARCHAR(100)    NOT NULL,
    payload         TEXT            NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    published       BOOLEAN         NOT NULL DEFAULT FALSE,
    published_at    TIMESTAMP
);

CREATE INDEX idx_outbox_unpublished ON outbox_events(published) WHERE published = FALSE;
