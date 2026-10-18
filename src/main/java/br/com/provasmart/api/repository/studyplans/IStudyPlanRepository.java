package br.com.provasmart.api.repository.studyplans;

import br.com.provasmart.api.domain.entity.studyplans.StudyPlanEntity;
import br.com.provasmart.api.domain.enums.StudyPlanStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IStudyPlanRepository extends JpaRepository<StudyPlanEntity, UUID> {

    Optional<StudyPlanEntity> findByStudent_IdAndStatus(UUID studentId, StudyPlanStatusEnum status);

    Optional<StudyPlanEntity> findByIdAndStudent_Id(UUID studyPlanId, UUID studentId);

    boolean existsBySimulation_Id(UUID simulationId);

    Page<StudyPlanEntity> findByStudent_Id(UUID studentId, Pageable pageable);

    Page<StudyPlanEntity> findByStudent_IdAndStatus(UUID studentId, StudyPlanStatusEnum status, Pageable pageable);
}
