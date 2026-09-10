package br.com.provasmart.api.repository.questions;

import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IQuestionRepository extends JpaRepository<QuestionEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"subject", "alternatives"})
    Page<QuestionEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"subject", "alternatives"})
    Page<QuestionEntity> findAllByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"subject", "alternatives"})
    Optional<QuestionEntity> findOneById(UUID id);

    @EntityGraph(attributePaths = {"subject", "subject.discipline", "alternatives"})
    List<QuestionEntity> findAllByActiveTrueAndSubjectDisciplineExamArea (ExamAreaEnum examArea);

}
