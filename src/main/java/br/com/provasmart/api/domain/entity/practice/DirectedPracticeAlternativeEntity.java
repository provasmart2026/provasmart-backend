package br.com.provasmart.api.domain.entity.practice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "directed_practice_alternatives")
public class DirectedPracticeAlternativeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_question_id", nullable = false)
    private DirectedPracticeQuestionEntity practiceQuestion;

    @Column(name = "original_alternative_id", nullable = false)
    private UUID originalAlternativeId;

    @Column(name = "letter", nullable = false, length = 1)
    private String letter;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "correct", nullable = false)
    private boolean correct;
}
