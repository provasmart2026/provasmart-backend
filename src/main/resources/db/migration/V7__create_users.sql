CREATE TABLE users
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    active     BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT ck_users_email_normalized CHECK (email = LOWER(TRIM(email))),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'ESTUDANTE'))
);
