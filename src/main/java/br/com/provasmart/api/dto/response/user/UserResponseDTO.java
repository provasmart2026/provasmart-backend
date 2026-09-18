package br.com.provasmart.api.dto.response.user;

import br.com.provasmart.api.domain.enums.RoleEnum;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        RoleEnum role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
