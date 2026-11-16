CREATE TABLE audit_logs
(
    id             UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    actor_id       UUID,
    actor_email    VARCHAR(255),
    action         VARCHAR(60)  NOT NULL,
    resource       VARCHAR(60)  NOT NULL,
    resource_id    UUID,
    request_method VARCHAR(10)  NOT NULL,
    endpoint       VARCHAR(255) NOT NULL,
    status_code    INTEGER      NOT NULL,
    success        BOOLEAN      NOT NULL,
    occurred_at    TIMESTAMP    NOT NULL
);

CREATE INDEX idx_audit_logs_occurred_at ON audit_logs (occurred_at DESC);
CREATE INDEX idx_audit_logs_actor_id ON audit_logs (actor_id);
