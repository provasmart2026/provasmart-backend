package br.com.provasmart.api.dto.response.practice;

import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resumo de uma prática direcionada para consulta do histórico")
public record DirectedPracticeSummaryResponseDTO(
        UUID id,
        ExamAreaEnum area,
        DirectedPracticeStatus status,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer totalQuestions,
        Integer answeredQuestions,
        @Schema(description = "Acertos; ausente para prática não finalizada", nullable = true)
        Integer correctAnswers,
        @Schema(description = "Erros; ausente para prática não finalizada", nullable = true)
        Integer incorrectAnswers,
        @Schema(description = "Percentual de acertos; ausente para prática não finalizada", nullable = true)
        Double accuracyPercentage
) {
}
