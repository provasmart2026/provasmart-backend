CREATE TABLE authentication_codes
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id    UUID        NOT NULL,
    code       VARCHAR(60) NOT NULL,
    purpose    VARCHAR(30) NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    used       BOOLEAN     NOT NULL,
    created_at TIMESTAMP   NOT NULL,

    CONSTRAINT fk_authentication_codes_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT ck_authentication_codes_purpose
        CHECK (purpose IN ('LOGIN_2FA', 'PASSWORD_RESET'))
);