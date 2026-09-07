package br.com.provasmart.api.repository.questions;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IDisciplineRepository extends JpaRepository<DisciplineEntity, UUID> {

    List<DisciplineEntity> findAllByExamAreaOrderByNameAsc(ExamAreaEnum examArea);
}
