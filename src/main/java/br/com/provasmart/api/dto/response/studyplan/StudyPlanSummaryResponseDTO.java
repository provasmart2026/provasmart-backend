package br.com.provasmart.api.dto.response.studyplan;

import br.com.provasmart.api.domain.enums.StudyPlanStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resumo de um plano para consulta do histórico")
public record StudyPlanSummaryResponseDTO(
        @Schema(description = "ID do plano")
        UUID id,

        @Schema(description = "ID do simulado de origem")
        UUID simulationId,

        @Schema(description = "Horas de estudo por dia")
        Integer dailyHours,

        @Schema(description = "Data inicial do cronograma")
        LocalDate startDate,

        @Schema(description = "Estado do plano")
        StudyPlanStatusEnum status,

        @Schema(description = "Data e hora de geração")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora de arquivamento; ausente para plano ativo")
        LocalDateTime archivedAt,

        @Schema(description = "Total de atividades")
        Integer totalActivities,

        @Schema(description = "Quantidade de atividades concluídas")
        Integer completedActivities,

        @Schema(description = "Percentual de conclusão, de 0 a 100")
        Double progressPercentage
) {
}