package br.com.provasmart.api.domain.entity.simulations;


import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "simulation_questions", uniqueConstraints = {@UniqueConstraint(name = "uk_simulation_question",
        columnNames = {"simulation_id", "question_id"}),
        @UniqueConstraint(name = "uk_simulation_question_order", columnNames = {"simulation_id", "position"})})
public class SimulationQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "simulation_id", nullable = false)
    private SimulationEntity simulation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Column(name = "position", nullable = false)
    private Integer position;

    @OneToOne(mappedBy = "simulationQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private SimulationAnswerEntity answer;

}
