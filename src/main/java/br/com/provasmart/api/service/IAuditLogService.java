package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.response.audit.AuditLogResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IAuditLogService {

    void record(UUID actorId, String actorEmail, String action, String resource, UUID resourceId,
                String requestMethod, String endpoint, int statusCode);

    Page<AuditLogResponseDTO> findAll(Pageable pageable);
}
