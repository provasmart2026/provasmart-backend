package br.com.provasmart.api.dto.response.simulation.performance;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import java.util.List;

@Schema(description = "Desempenho por disciplina")
public record SimulationDisciplinePerformanceDTO(
        @Schema(description = "ID da disciplina", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID disciplineId,

        @Schema(description = "Nome da disciplina", example = "Matemática")
        String disciplineName,

        @Schema(description = "Total de questões", example = "40")
        Integer totalQuestions,

        @Schema(description = "Total de respostas corretas", example = "30")
        Integer correctAnswers,

        @Schema(description = "Total de respostas incorretas", example = "10")
        Integer incorrectAnswers,

        @Schema(description = "Percentual de acertos, calculado por acertos /" +
                " total de questões × 100", example = "75.0")
        Double accuracyPercentage,

        @Schema(description = "Assuntos da disciplina que participaram do simulado")
        List<SimulationSubjectPerformanceDTO> subjects
) {
}