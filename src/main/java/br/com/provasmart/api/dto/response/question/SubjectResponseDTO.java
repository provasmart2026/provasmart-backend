package br.com.provasmart.api.dto.response.question;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO de resposta para uma matéria")
public record SubjectResponseDTO(
        @Schema(description = "ID da matéria", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome da matéria", example = "Física")
        String name
) {
}
