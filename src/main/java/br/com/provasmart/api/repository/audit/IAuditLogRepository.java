package br.com.provasmart.api.repository.audit;

import br.com.provasmart.api.domain.entity.audit.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}
