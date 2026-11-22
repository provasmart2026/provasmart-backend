package br.com.provasmart.api.repository.questions;

import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IQuestionRepository extends JpaRepository<QuestionEntity, UUID> {


    Page<QuestionEntity> findAllByOrderByActiveDesc(Pageable pageable);

    Page<QuestionEntity> findAllByActiveTrue(Pageable pageable);

    Optional<QuestionEntity> findOneById(UUID id);

    @Query(value = """
            SELECT under_report_review
            FROM questions
            WHERE id = :questionId
            FOR UPDATE
            """, nativeQuery = true)
    Optional<Boolean> findReportReviewStatusForUpdate(@Param("questionId") UUID questionId);

    @Modifying
    @Query(value = """
            UPDATE questions
            SET active = FALSE, under_report_review = TRUE, updated_at = :updatedAt
            WHERE id = :questionId
            """, nativeQuery = true)
    void deactivateAndStartReportReview(@Param("questionId") UUID questionId,
                                        @Param("updatedAt") LocalDateTime updatedAt);

    List<QuestionEntity> findAllByActiveTrueAndSubjectDisciplineExamArea (ExamAreaEnum examArea);

}
