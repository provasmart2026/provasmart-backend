ALTER TABLE simulation_questions
    ADD COLUMN exam_area_snapshot VARCHAR(50),
    ADD COLUMN discipline_id_snapshot UUID,
    ADD COLUMN discipline_name_snapshot VARCHAR(100),
    ADD COLUMN subject_id_snapshot UUID,
    ADD COLUMN subject_name_snapshot VARCHAR(150);