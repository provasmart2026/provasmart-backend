package br.com.provasmart.api.dto.request.studyplan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Dados para geração de um plano de estudos")
public record StudyPlanCreateRequestDTO(

        @NotNull(message = "O simulado de referência é obrigatório")
        @Schema(description = "ID do último Simulado Geral finalizado")
        UUID simulationId,

        @NotNull(message = "A carga diária é obrigatória")
        @Min(value = 1, message = "A carga diária deve ser de pelo menos 1 hora")
        @Max(value = 3, message = "A carga diária deve ser de no máximo 3 horas")
        @Schema(description = "Horas de estudo por dia", example = "2")
        Integer dailyHours,

        @Schema(description = "Indica se o estudante confirmou a substituição do plano ativo; somente true confirma")
        Boolean replaceCurrentPlan
) {
}