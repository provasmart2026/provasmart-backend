package br.com.provasmart.api.dto.response.dashboard;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Desempenho acumulado por área do ENEM")
public record DashboardAreaPerformanceDTO(
        @Schema(description = "Área do ENEM", example = "MATEMATICA")
        ExamAreaEnum examArea,

        @Schema(description = "Total de questões respondidas", example = "40")
        int totalQuestions,

        @Schema(description = "Total de respostas corretas", example = "24")
        int correctAnswers,

        @Schema(description = "Total de respostas incorretas", example = "16")
        int incorrectAnswers,

        @Schema(description = "Percentual de acertos sobre as questões respondidas", example = "60.0")
        Double accuracyPercentage,

        @Schema(description = "Classificação do desempenho da área", example = "Dificuldade média")
        String classification
) {
}
