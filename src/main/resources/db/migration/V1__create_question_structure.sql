CREATE
EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE exam_areas
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE disciplines
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(100) NOT NULL,
    exam_area_id UUID         NOT NULL,

    CONSTRAINT fk_discipline_exam_area
        FOREIGN KEY (exam_area_id)
            REFERENCES exam_areas (id),

    CONSTRAINT uk_discipline_name_area
        UNIQUE (name, exam_area_id)
);

CREATE TABLE subjects
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(150) NOT NULL,
    discipline_id UUID         NOT NULL,

    CONSTRAINT fk_subject_discipline
        FOREIGN KEY (discipline_id)
            REFERENCES disciplines (id),

    CONSTRAINT uk_subject_name_discipline
        UNIQUE (name, discipline_id)
);

CREATE TABLE questions
(
    id          UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    statement   TEXT      NOT NULL,
    explanation TEXT      NOT NULL,
    subject_id  UUID      NOT NULL,
    active      BOOLEAN   NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,

    CONSTRAINT fk_question_subject
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
);

CREATE TABLE alternatives
(
    id          UUID PRIMARY KEY    DEFAULT gen_random_uuid(),
    question_id UUID       NOT NULL,
    letter      VARCHAR(1) NOT NULL,
    text        TEXT       NOT NULL,
    correct     BOOLEAN    NOT NULL,

    CONSTRAINT fk_alternative_question
        FOREIGN KEY (question_id)
            REFERENCES questions (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_alternative_question_letter
        UNIQUE (question_id, letter),

    CONSTRAINT ck_alternative_letter
        CHECK (letter IN ('A', 'B', 'C', 'D', 'E'))
);