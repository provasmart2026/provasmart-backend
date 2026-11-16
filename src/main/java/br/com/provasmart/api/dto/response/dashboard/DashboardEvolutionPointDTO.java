package br.com.provasmart.api.dto.response.dashboard;

import java.time.LocalDateTime;

public record DashboardEvolutionPointDTO(
        LocalDateTime finishedAt,
        int totalQuestions,
        int correctAnswers,
        double accuracyPercentage
) {
}
