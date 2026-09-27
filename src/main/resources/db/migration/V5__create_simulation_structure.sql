CREATE TABLE simulations
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID        NOT NULL,
    status      VARCHAR(20) NOT NULL,
    started_at  TIMESTAMP   NOT NULL,
    finished_at TIMESTAMP,

    CONSTRAINT fk_simulations_student FOREIGN KEY (student_id) REFERENCES users (id)
);

CREATE TABLE simulation_questions
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    simulation_id UUID    NOT NULL,
    question_id   UUID    NOT NULL,
    position      INTEGER NOT NULL,

    CONSTRAINT fk_simulation_question_simulation
        FOREIGN KEY (simulation_id)
            REFERENCES simulations (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_simulation_question_question
        FOREIGN KEY (question_id)
            REFERENCES questions (id),

    CONSTRAINT uk_simulation_question
        UNIQUE (simulation_id, question_id),

    CONSTRAINT uk_simulation_position
        UNIQUE (simulation_id, position)
);

CREATE TABLE simulation_answers
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    simulation_question_id  UUID      NOT NULL,
    selected_alternative_id UUID      NOT NULL,
    answered_at             TIMESTAMP NOT NULL,

    CONSTRAINT fk_simulation_answer_simulation_question
        FOREIGN KEY (simulation_question_id)
            REFERENCES simulation_questions (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_simulation_answer_alternative
        FOREIGN KEY (selected_alternative_id)
            REFERENCES alternatives (id),

    CONSTRAINT uk_simulation_answer_question
        UNIQUE (simulation_question_id)
);