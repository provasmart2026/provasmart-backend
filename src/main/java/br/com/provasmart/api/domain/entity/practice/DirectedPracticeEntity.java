package br.com.provasmart.api.domain.entity.practice;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
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
@Table(name = "directed_practices")
public class DirectedPracticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_simulation_id")
    private SimulationEntity sourceSimulation;

    @Enumerated(EnumType.STRING)
    @Column(name = "area", nullable = false, length = 50)
    private ExamAreaEnum area;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DirectedPracticeStatus status;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "practice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<DirectedPracticeQuestionEntity> questions = new ArrayList<>();
}
