package br.com.provasmart.api.dto.response.legal;

import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record LegalDocumentResponseDTO(
        UUID id,
        LegalDocumentTypeEnum type,
        String version,
        String title,
        String content,
        LocalDateTime effectiveAt,
        LocalDateTime createdAt
) {
}
