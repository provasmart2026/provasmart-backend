package br.com.provasmart.api.dto.response.question;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO para representar uma alternativa de questão")
public record AlternativeResponseDTO(

        @Schema(description = "O ID da alternativa", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "A letra da alternativa", example = "A")
        String letter,

        @Schema(description = "O texto da alternativa", example = "Esta é a alternativa A")
        String text,

        @Schema(description = "Indica se a alternativa é correta", example = "true")
        boolean correct
) {
}
