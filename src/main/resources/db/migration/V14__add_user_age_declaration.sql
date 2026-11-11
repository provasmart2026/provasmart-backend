-- Existing accounts have no recorded declaration; do not fabricate acceptance.
ALTER TABLE users
    ADD COLUMN age_declaration_accepted BOOLEAN,
    ADD COLUMN age_declaration_accepted_at TIMESTAMP;
