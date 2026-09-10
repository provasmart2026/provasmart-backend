package br.com.provasmart.api.domain.entity.simulations;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
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
@Table(name = "simulation_answers", uniqueConstraints = {@UniqueConstraint(name = "uk_simulation_answer_question",
        columnNames = "simulation_question_id")})
public class SimulationAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_question_id", nullable = false)
    private SimulationQuestionEntity simulationQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_alternative_id", nullable = false)
    private AlternativeEntity selectedAlternative;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;
}
