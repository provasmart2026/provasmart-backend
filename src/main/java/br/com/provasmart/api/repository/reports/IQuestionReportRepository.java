package br.com.provasmart.api.repository.reports;

import br.com.provasmart.api.domain.entity.reports.QuestionReportEntity;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IQuestionReportRepository extends JpaRepository<QuestionReportEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from QuestionReportEntity r where r.id = :id")
    Optional<QuestionReportEntity> findByIdForUpdate(@Param("id") UUID id);

    @EntityGraph(attributePaths = "student")
    Page<QuestionReportEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "student")
    Page<QuestionReportEntity> findByStatusOrderByCreatedAtDesc(QuestionReportStatusEnum status, Pageable pageable);

    long countByStatus(QuestionReportStatusEnum status);

    boolean existsByStudent_IdAndOriginalQuestionIdAndStatus(
            UUID studentId, UUID originalQuestionId, QuestionReportStatusEnum status);
}