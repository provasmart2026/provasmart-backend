package br.com.provasmart.api.dto.response.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Desempenho acumulado do estudante em uma modalidade")
public record StudentDashboardResponseDTO(
        @Schema(description = "Total de atividades finalizadas na modalidade", example = "2")
        int completedActivities,

        @Schema(description = "Total de questões respondidas", example = "40")
        int totalQuestions,

        @Schema(description = "Total de respostas corretas", example = "24")
        int correctAnswers,

        @Schema(description = "Total de respostas incorretas", example = "16")
        int incorrectAnswers,

        @Schema(description = "Percentual de acertos sobre as questões respondidas", example = "60.0")
        Double accuracyPercentage,

        @Schema(description = "Desempenho acumulado por área do ENEM")
        List<DashboardAreaPerformanceDTO> areas,

        @Schema(description = "Evolução do percentual de acertos por atividade finalizada")
        List<DashboardEvolutionPointDTO> evolution
) {
}
