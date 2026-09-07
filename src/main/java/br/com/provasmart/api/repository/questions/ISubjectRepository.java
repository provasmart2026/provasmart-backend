package br.com.provasmart.api.repository.questions;

import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ISubjectRepository extends JpaRepository<SubjectEntity, UUID> {

    List<SubjectEntity> findAllByDiscipline_IdOrderByNameAsc(UUID disciplineId);

    boolean existsByNameIgnoreCaseAndDiscipline_Id(String name, UUID disciplineId);
}
