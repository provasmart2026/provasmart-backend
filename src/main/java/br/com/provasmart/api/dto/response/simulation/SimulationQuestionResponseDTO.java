package br.com.provasmart.api.dto.response.simulation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "DTO para resposta de questão de simulação")
public record SimulationQuestionResponseDTO(

        @Schema(description = "ID da questão", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Posição da questão na simulação", example = "1")
        Integer position,

        @Schema(description = "ID da questão original", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID questionId,

        @Schema(description = "Enunciado da questão", example = "Qual é a capital da França?")
        String statement,

        @Schema(description = "Lista de alternativas da questão")
        List<SimulationAlternativeResponseDTO> alternatives,

        @Schema(description = "ID da alternativa selecionada pelo usuário, se houver", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID selectedAlternativeId
) {
}
