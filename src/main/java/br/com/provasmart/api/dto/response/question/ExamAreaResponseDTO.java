package br.com.provasmart.api.dto.response.question;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO de resposta para uma área do ENEM")
public record ExamAreaResponseDTO(
        @Schema(description = "ID da área", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome da área", example = "Ciências Humanas")
        String name
) {
}
