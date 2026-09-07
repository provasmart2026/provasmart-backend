package br.com.provasmart.api.repository.questions;

import br.com.provasmart.api.domain.entity.questions.ExamAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IExamAreaRepository extends JpaRepository<ExamAreaEntity, UUID> {

    List<ExamAreaEntity> findAllByOrderByNameAsc();
}
