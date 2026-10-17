package br.com.provasmart.api.repository.practice;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDirectedPracticeRepository extends JpaRepository<DirectedPracticeEntity, UUID> {
    boolean existsByStudent_IdAndStatus(UUID studentId, DirectedPracticeStatus status);
    Optional<DirectedPracticeEntity> findByIdAndStudent_Id(UUID id, UUID studentId);
    List<DirectedPracticeEntity> findByStudent_IdOrderByStartedAtDesc(UUID studentId);
    long countByStudent_IdAndArea(UUID studentId, ExamAreaEnum area);
    Optional<DirectedPracticeEntity> findFirstByStudent_IdAndAreaOrderByStartedAtDesc(UUID studentId, ExamAreaEnum area);
}
