package br.com.provasmart.api.dto.response.simulation.performance;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Resultado geral do simulado")
public record SimulationPerformanceSummaryDTO(
        @Schema(description = "Total de questões", example = "40")
        Integer totalQuestions,

        @Schema(description = "Total de respostas corretas", example = "30")
        Integer correctAnswers,

        @Schema(description = "Total de respostas incorretas", example = "10")
        Integer incorrectAnswers,

        @Schema(description = "Percentual de acertos, calculado por acertos /" +
                " total de questões × 100", example = "75.0")
        Double accuracyPercentage
) {
}