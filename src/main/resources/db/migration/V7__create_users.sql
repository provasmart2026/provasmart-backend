CREATE TABLE users
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    email      VARCHAR(254) NOT NULL,
    password   VARCHAR(60) NOT NULL,
    role       VARCHAR(20) NOT NULL DEFAULT 'ESTUDANTE',
    active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_email_normalized CHECK (email = LOWER(TRIM(email))),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'ESTUDANTE'))
);
