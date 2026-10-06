ALTER TABLE authentication_codes
    ADD COLUMN invalid_attempts INTEGER NOT NULL DEFAULT 0
    CHECK (invalid_attempts >= 0);
