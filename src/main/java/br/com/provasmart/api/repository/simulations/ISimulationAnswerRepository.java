package br.com.provasmart.api.repository.simulations;

import br.com.provasmart.api.domain.entity.simulations.SimulationAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISimulationAnswerRepository extends JpaRepository<SimulationAnswerEntity, UUID> {

    Optional<SimulationAnswerEntity> findBySimulationQuestionId(UUID simulationQuestionId);
}
