package br.com.provasmart.api.repository.simulations;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISimulationRepository extends JpaRepository<SimulationEntity, UUID> {

    boolean existsByStudent_IdAndStatus(UUID studentId, SimulationStatusEnum status);

    Optional<SimulationEntity> findByIdAndStudent_Id(UUID simulationId, UUID studentId);
}
