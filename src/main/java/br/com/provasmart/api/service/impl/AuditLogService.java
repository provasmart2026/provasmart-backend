package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.audit.AuditLogEntity;
import br.com.provasmart.api.dto.response.audit.AuditLogResponseDTO;
import br.com.provasmart.api.repository.audit.IAuditLogRepository;
import br.com.provasmart.api.service.IAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService implements IAuditLogService {

    private final IAuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID actorId, String actorEmail, String action, String resource, UUID resourceId,
                       String requestMethod, String endpoint, int statusCode) {
        var auditLog = new AuditLogEntity();
        auditLog.setActorId(actorId);
        auditLog.setActorEmail(actorEmail);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setResourceId(resourceId);
        auditLog.setRequestMethod(requestMethod);
        auditLog.setEndpoint(endpoint);
        auditLog.setStatusCode(statusCode);
        auditLog.setSuccess(statusCode < 400);
        auditLog.setOccurredAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDTO> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(this::toResponseDTO);
    }

    private AuditLogResponseDTO toResponseDTO(AuditLogEntity auditLog) {
        return new AuditLogResponseDTO(
                auditLog.getId(),
                auditLog.getActorId(),
                auditLog.getActorEmail(),
                auditLog.getAction(),
                auditLog.getResource(),
                auditLog.getResourceId(),
                auditLog.getRequestMethod(),
                auditLog.getEndpoint(),
                auditLog.getStatusCode(),
                auditLog.isSuccess(),
                auditLog.getOccurredAt()
        );
    }
}
