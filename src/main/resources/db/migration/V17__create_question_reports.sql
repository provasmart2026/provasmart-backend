CREATE TABLE question_reports
(
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    original_question_id   UUID NOT NULL,
    origin                 VARCHAR(30) NOT NULL,
    simulation_question_id UUID REFERENCES simulation_questions(id) ON DELETE CASCADE,
    practice_question_id   UUID REFERENCES directed_practice_questions(id) ON DELETE CASCADE,
    reason                 VARCHAR(40) NOT NULL,
    description            VARCHAR(1000) NOT NULL,
    content_snapshot       TEXT NOT NULL,
    status                 VARCHAR(20) NOT NULL,
    created_at             TIMESTAMP NOT NULL,
    updated_at             TIMESTAMP,

    CONSTRAINT ck_question_report_origin CHECK (
        (origin = 'SIMULADO' AND simulation_question_id IS NOT NULL AND practice_question_id IS NULL)
        OR
        (origin = 'PRATICA_DIRECIONADA' AND practice_question_id IS NOT NULL AND simulation_question_id IS NULL)
    ),
    CONSTRAINT ck_question_report_reason CHECK (reason IN (
        'ENUNCIADO_INCORRETO', 'ALTERNATIVAS_COM_PROBLEMA', 'GABARITO_INCORRETO',
        'EXPLICACAO_INCORRETA', 'OUTRO'
    )),
    CONSTRAINT ck_question_report_status CHECK (status IN ('PENDENTE', 'RESOLVIDO', 'DESCARTADO')),
    CONSTRAINT ck_question_report_description CHECK (description ~ '[^[:space:]]'),
    CONSTRAINT ck_question_report_content CHECK (content_snapshot ~ '[^[:space:]]')
);

CREATE INDEX idx_question_report_status_created_at
    ON question_reports(status, created_at DESC);