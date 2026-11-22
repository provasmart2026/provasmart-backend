package br.com.provasmart.api.dto.response.user;

import br.com.provasmart.api.domain.enums.RoleEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAdminSummaryResponseDTO(
        UUID id,
        String name,
        RoleEnum role,
        boolean active,
        boolean deletionRequested,
        LocalDateTime deletionRequestedAt,
        LocalDateTime createdAt
) {
}
