package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.audit.AuditLogEntity;
import br.com.provasmart.api.dto.response.audit.AuditLogEmailResponseDTO;
import br.com.provasmart.api.dto.response.audit.AuditLogResponseDTO;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.repository.audit.IAuditLogRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.IAuditLogService;
import br.com.provasmart.api.service.ICurrentActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService implements IAuditLogService {

    private final IAuditLogRepository auditLogRepository;
    private final IUserRepository userRepository;
    private final ICurrentActorService currentActorService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID actorId, String actorEmail, String action, String resource, UUID resourceId,
                       String requestMethod, String endpoint, int statusCode) {
        var auditLog = new AuditLogEntity();
        auditLog.setActorId(actorId);
        auditLog.setActorEmail(actorEmail);
        if (actorId != null) {
            userRepository.findById(actorId).ifPresent(actor -> {
                auditLog.setActorName(actor.getName());
                if (auditLog.getActorEmail() == null) {
                    auditLog.setActorEmail(actor.getEmail());
                }
            });
        }
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

    @Override
    @Transactional
    public AuditLogEmailResponseDTO revealEmail(UUID id) {
        var email = auditLogRepository.findById(id)
                .map(this::displayEmail)
                .filter(StringUtils::hasText)
                .orElseThrow(() -> new NotFoundException("E-mail indisponível para este registro de auditoria."));

        var adminId = currentActorService.getCurrentUserId();
        record(adminId, null, "REVEAL_AUDIT_EMAIL", "AUDIT_LOGS", id,
                "GET", "/audit-logs/" + id + "/email", 200);

        return new AuditLogEmailResponseDTO(email);
    }

    private AuditLogResponseDTO toResponseDTO(AuditLogEntity auditLog) {
        return new AuditLogResponseDTO(
                auditLog.getId(),
                auditLog.getActorId(),
                auditLog.getActorName(),
                StringUtils.hasText(displayEmail(auditLog)),
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

    private String displayEmail(AuditLogEntity auditLog) {
        var action = auditLog.getAction();
        if (auditLog.getActorId() != null || "LOGIN".equals(action) || "VERIFY_2FA".equals(action)) {
            return auditLog.getActorEmail();
        }
        return null;
    }
}
