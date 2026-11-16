package br.com.provasmart.api.repository.legal;

import br.com.provasmart.api.domain.entity.legal.LegalDocumentEntity;
import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ILegalDocumentRepository extends JpaRepository<LegalDocumentEntity, UUID> {

    Optional<LegalDocumentEntity> findFirstByTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDescCreatedAtDescIdDesc(
            LegalDocumentTypeEnum type, LocalDateTime effectiveAt);
}
