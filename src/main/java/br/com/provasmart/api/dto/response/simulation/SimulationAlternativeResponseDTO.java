package br.com.provasmart.api.dto.response.simulation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO para resposta de alternativa de simulação")
public record SimulationAlternativeResponseDTO(

        @Schema(description = "ID da alternativa", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Letra da alternativa", example = "A")
        String letter,

        @Schema(description = "Texto da alternativa", example = "Lorem ipsum dolor sit amet")
        String text
) {
}
