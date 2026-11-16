package br.com.provasmart.api.domain.entity.reports;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.enums.QuestionReportOriginEnum;
import br.com.provasmart.api.domain.enums.QuestionReportReasonEnum;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "question_reports")
public class QuestionReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, updatable = false)
    private UserEntity student;

    @Column(name = "original_question_id", nullable = false, updatable = false)
    private UUID originalQuestionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false, length = 30, updatable = false)
    private QuestionReportOriginEnum origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_question_id", updatable = false)
    private SimulationQuestionEntity simulationQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_question_id", updatable = false)
    private DirectedPracticeQuestionEntity practiceQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 40, updatable = false)
    private QuestionReportReasonEnum reason;

    @Column(name = "description", nullable = false, length = 1000, updatable = false)
    private String description;

    @Column(name = "content_snapshot", nullable = false, columnDefinition = "TEXT", updatable = false)
    private String contentSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuestionReportStatusEnum status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}