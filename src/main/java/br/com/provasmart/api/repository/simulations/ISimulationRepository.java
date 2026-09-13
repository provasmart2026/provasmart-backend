package br.com.provasmart.api.repository.simulations;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISimulationRepository extends JpaRepository<SimulationEntity, UUID> {

    boolean existsByStudentIdAndStatus(UUID studentID, SimulationStatusEnum status);

    Optional<SimulationEntity> findByStudentIdAndStatus(UUID studentID, SimulationStatusEnum  status);
}
