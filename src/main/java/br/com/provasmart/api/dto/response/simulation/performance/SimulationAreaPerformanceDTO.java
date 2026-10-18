package br.com.provasmart.api.dto.response.simulation.performance;

import io.swagger.v3.oas.annotations.media.Schema;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import java.util.List;

@Schema(description = "Desempenho por área")
public record SimulationAreaPerformanceDTO(
        @Schema(description = "Área do ENEM", example = "MATEMATICA")
        ExamAreaEnum examArea,

        @Schema(description = "Total de questões", example = "40")
        Integer totalQuestions,

        @Schema(description = "Total de respostas corretas", example = "30")
        Integer correctAnswers,

        @Schema(description = "Total de respostas incorretas", example = "10")
        Integer incorrectAnswers,

        @Schema(description = "Percentual de acertos, calculado por acertos /" +
                " total de questões × 100", example = "75.0")
        Double accuracyPercentage,

        @Schema(description = "Disciplinas da área que participaram do simulado")
        List<SimulationDisciplinePerformanceDTO> disciplines
) {
}