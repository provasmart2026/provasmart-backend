package br.com.provasmart.api.repository.simulations;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISimulationRepository extends JpaRepository<SimulationEntity, UUID> {

    Optional<SimulationEntity> findByStudent_IdAndStatus(UUID studentId, SimulationStatusEnum status);

    Optional<SimulationEntity> findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(
            UUID studentId, SimulationStatusEnum status);

    boolean existsByStudent_IdAndStatus(UUID studentId, SimulationStatusEnum status);

    Optional<SimulationEntity> findByIdAndStudent_Id(UUID simulationId, UUID studentId);

    @Query(value = "SELECT * FROM simulations WHERE id = :simulationId AND student_id = :studentId FOR UPDATE",
            nativeQuery = true)
    Optional<SimulationEntity> findByIdAndStudentIdForUpdate(@Param("simulationId") UUID simulationId,
                                                           @Param("studentId") UUID studentId);
}
