package br.com.provasmart.api.domain.entity.studyplans;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.StudyPlanActivityTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "study_plan_activities", uniqueConstraints = {
        @UniqueConstraint(name = "uk_study_plan_activity_position",
                columnNames = {"study_plan_id", "day_number", "position"})
})
public class StudyPlanActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_plan_id", nullable = false, updatable = false)
    private StudyPlanEntity studyPlan;

    @Column(name = "day_number", nullable = false, updatable = false)
    private Integer dayNumber;

    @Column(name = "position", nullable = false, updatable = false)
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_area", nullable = false, length = 50, updatable = false)
    private ExamAreaEnum examArea;

    @Column(name = "discipline_id_snapshot", nullable = false, updatable = false)
    private UUID disciplineIdSnapshot;

    @Column(name = "discipline_name_snapshot", nullable = false, length = 100, updatable = false)
    private String disciplineNameSnapshot;

    @Column(name = "subject_id_snapshot", nullable = false, updatable = false)
    private UUID subjectIdSnapshot;

    @Column(name = "subject_name_snapshot", nullable = false, length = 150, updatable = false)
    private String subjectNameSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20, updatable = false)
    private StudyPlanActivityTypeEnum type;

    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
