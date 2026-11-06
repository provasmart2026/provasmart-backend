package br.com.provasmart.api.dto.response.simulation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Correção individual de uma questão do simulado finalizado")
public record SimulationQuestionCorrectionResponseDTO(

        @Schema(description = "ID da questão no simulado")
        UUID simulationQuestionId,

        @Schema(description = "Posição da questão no simulado", example = "1")
        Integer position,

        @Schema(description = "Enunciado da questão")
        String statement,

        @Schema(description = "Alternativas da questão")
        List<SimulationAlternativeResponseDTO> alternatives,

        @Schema(description = "ID da alternativa marcada pelo estudante", nullable = true)
        UUID selectedAlternativeId,

        @Schema(description = "ID da alternativa correta registrada no simulado", nullable = true)
        UUID correctAlternativeId,

        @Schema(description = "Acerto registrado na resposta. Nulo quando indisponível", nullable = true)
        Boolean isCorrect,

        @Schema(description = "Explicação da questão")
        String explanation
) {
}
