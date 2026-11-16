package br.com.provasmart.api.dto.response.simulation;

import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resumo de um simulado para consulta do histórico, sem a lista de questões")
public record SimulationSummaryResponseDTO(
        @Schema(description = "ID do simulado")
        UUID id,

        @Schema(description = "Estado do simulado", example = "FINALIZADO")
        SimulationStatusEnum status,

        @Schema(description = "Data e hora de início")
        LocalDateTime startedAt,

        @Schema(description = "Data e hora de encerramento")
        LocalDateTime finishedAt,

        @Schema(description = "Total de questões do simulado", example = "40")
        Integer totalQuestions,

        @Schema(description = "Quantidade de questões respondidas")
        Integer answeredQuestions,

        @Schema(description = "Quantidade de acertos; ausente para simulado não finalizado", nullable = true)
        Integer correctAnswers,

        @Schema(description = "Quantidade de erros; ausente para simulado não finalizado", nullable = true)
        Integer incorrectAnswers,

        @Schema(description = "Percentual de acertos, de 0 a 100; ausente para simulado não finalizado", nullable = true)
        Double accuracyPercentage
) {
}
