package br.com.provasmart.api.dto.response.question;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO de resposta para uma disciplina")
public record DisciplineResponseDTO(
        @Schema(description = "ID da disciplina", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome da disciplina", example = "Matemática")
        String name
) {
}
