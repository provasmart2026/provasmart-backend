package br.com.provasmart.api.domain.entity.practice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "directed_practice_questions")
public class DirectedPracticeQuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_id", nullable = false)
    private DirectedPracticeEntity practice;

    @Column(name = "original_question_id", nullable = false)
    private UUID originalQuestionId;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "statement", nullable = false, columnDefinition = "TEXT")
    private String statement;

    @Column(name = "explanation", nullable = false, columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "discipline_name", nullable = false, length = 100)
    private String disciplineName;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "selected_alternative_id")
    private UUID selectedAlternativeId;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "correct_at_finish")
    private Boolean correctAtFinish;

    @OneToMany(mappedBy = "practiceQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("letter ASC")
    private List<DirectedPracticeAlternativeEntity> alternatives = new ArrayList<>();
}
