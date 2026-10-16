package br.com.provasmart.api.dto.response.studyplan;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Disponibilidade e informações para gerar um plano de estudos")
public record StudyPlanGenerationContextResponseDTO(
        @Schema(description = "Indica se o estudante pode gerar um plano")
        Boolean canGenerate,

        @Schema(description = "Motivo do impedimento; ausente quando a geração estiver disponível")
        String blockingReason,

        @Schema(description = "ID do último Simulado Geral finalizado; ausente quando não houver")
        UUID simulationId,

        @Schema(description = "Quantidade de assuntos distintos com erros no simulado de referência")
        Integer eligibleSubjectCount,

        @Schema(description = "ID do plano ativo; ausente quando não houver")
        UUID activePlanId
) {
}