ALTER TABLE users
    ADD COLUMN deletion_requested BOOLEAN NOT NULl,
    ADD COLUMN deletion_requested_at TIMESTAMP;