package br.com.provasmart.api.dto.response.simulation.performance;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Desempenho por assunto")
public record SimulationSubjectPerformanceDTO(
        @Schema(description = "ID do assunto", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID subjectId,

        @Schema(description = "Nome do assunto", example = "Álgebra")
        String subjectName,

        @Schema(description = "Total de questões", example = "40")
        Integer totalQuestions,

        @Schema(description = "Total de respostas corretas", example = "30")
        Integer correctAnswers,

        @Schema(description = "Total de respostas incorretas", example = "10")
        Integer incorrectAnswers,

        @Schema(description = "Percentual de acertos, calculado por acertos / " +
                "total de questões × 100", example = "75.0")
        Double accuracyPercentage
) {
}