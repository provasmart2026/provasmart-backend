CREATE TABLE study_plans
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id    UUID        NOT NULL,
    simulation_id UUID        NOT NULL UNIQUE,
    daily_hours   INTEGER     NOT NULL,
    start_date    DATE        NOT NULL,
    status        VARCHAR(20) NOT NULL,
    created_at    TIMESTAMP   NOT NULL,
    archived_at   TIMESTAMP,

    CONSTRAINT fk_study_plan_student FOREIGN KEY (student_id) REFERENCES users (id),
    CONSTRAINT fk_study_plan_simulation FOREIGN KEY (simulation_id) REFERENCES simulations (id),
    CONSTRAINT ck_study_plan_daily_hours CHECK (daily_hours BETWEEN 1 AND 3),
    CONSTRAINT ck_study_plan_status CHECK (status IN ('ATIVO', 'ARQUIVADO'))
);

CREATE UNIQUE INDEX uk_study_plan_active_student ON study_plans (student_id)
    WHERE status = 'ATIVO';

CREATE INDEX idx_study_plans_student_created_at ON study_plans (student_id, created_at DESC);

CREATE TABLE study_plan_activities
(
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    study_plan_id            UUID         NOT NULL,
    day_number               INTEGER      NOT NULL,
    position                 INTEGER      NOT NULL,
    exam_area                VARCHAR(50)  NOT NULL,
    discipline_id_snapshot   UUID         NOT NULL,
    discipline_name_snapshot VARCHAR(100) NOT NULL,
    subject_id_snapshot      UUID         NOT NULL,
    subject_name_snapshot    VARCHAR(150) NOT NULL,
    type                     VARCHAR(20)  NOT NULL,
    completed                BOOLEAN      NOT NULL DEFAULT FALSE,
    completed_at             TIMESTAMP,

    CONSTRAINT fk_study_plan_activity_plan FOREIGN KEY (study_plan_id) REFERENCES study_plans (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_study_plan_activity_position UNIQUE (study_plan_id, day_number, position),
    CONSTRAINT ck_study_plan_activity_day CHECK (day_number BETWEEN 1 AND 7),
    CONSTRAINT ck_study_plan_activity_position CHECK (position BETWEEN 1 AND 3),
    CONSTRAINT ck_study_plan_activity_type CHECK (type IN ('ESTUDO', 'REVISAO'))
);
