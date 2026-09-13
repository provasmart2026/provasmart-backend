package br.com.provasmart.api.repository.simulations;

import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ISimulationQuestionRepository extends JpaRepository<SimulationQuestionEntity, UUID> {

    List<SimulationQuestionEntity> findAllBySimulationIdOrderByPositionAsc(UUID simulationId);

    boolean existsBySimulationIdAndQuestionId(UUID simulationId, UUID questionId);
}
