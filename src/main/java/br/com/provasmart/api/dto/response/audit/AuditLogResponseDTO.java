package br.com.provasmart.api.dto.response.audit;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponseDTO(
        UUID id,
        UUID actorId,
        String actorEmail,
        String action,
        String resource,
        UUID resourceId,
        String requestMethod,
        String endpoint,
        int statusCode,
        boolean success,
        LocalDateTime occurredAt
) {
}
