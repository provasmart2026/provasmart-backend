package br.com.provasmart.api.repository.questions;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IAlternativeRepository extends JpaRepository<AlternativeEntity, UUID> {
}
