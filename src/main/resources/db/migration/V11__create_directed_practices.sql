CREATE TABLE directed_practices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source_simulation_id UUID REFERENCES simulations(id) ON DELETE SET NULL,
    area VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP
);

CREATE UNIQUE INDEX uk_directed_practice_in_progress
    ON directed_practices(student_id) WHERE status = 'EM_ANDAMENTO';

CREATE TABLE directed_practice_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    practice_id UUID NOT NULL REFERENCES directed_practices(id) ON DELETE CASCADE,
    original_question_id UUID NOT NULL,
    position INTEGER NOT NULL,
    statement TEXT NOT NULL,
    explanation TEXT NOT NULL,
    discipline_name VARCHAR(100) NOT NULL,
    subject_name VARCHAR(150) NOT NULL,
    selected_alternative_id UUID,
    answered_at TIMESTAMP,
    correct_at_finish BOOLEAN,
    CONSTRAINT uk_directed_practice_question UNIQUE(practice_id, original_question_id),
    CONSTRAINT uk_directed_practice_position UNIQUE(practice_id, position)
);

CREATE TABLE directed_practice_alternatives (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    practice_question_id UUID NOT NULL REFERENCES directed_practice_questions(id) ON DELETE CASCADE,
    original_alternative_id UUID NOT NULL,
    letter VARCHAR(1) NOT NULL,
    text TEXT NOT NULL,
    correct BOOLEAN NOT NULL,
    CONSTRAINT uk_directed_practice_alternative UNIQUE(practice_question_id, original_alternative_id)
);
