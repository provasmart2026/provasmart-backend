package br.com.provasmart.api.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO para resposta de erro da API")
public record ErrorResponseDTO(

        @Schema(description = "Data e hora do erro", example = "2026-09-24T20:45:00")
        LocalDateTime timestamp,

        @Schema(description = "Código HTTP da resposta", example = "404")
        int status,

        @Schema(description = "Descrição do status HTTP", example = "Not Found")
        String error,

        @Schema(description = "Mensagem do erro", example = "Questão não encontrada.")
        String message,

        @Schema(description = "Caminho da requisição", example = "/questions/123e4567-e89b-12d3-a456-426614174000")
        String path
) {
}
