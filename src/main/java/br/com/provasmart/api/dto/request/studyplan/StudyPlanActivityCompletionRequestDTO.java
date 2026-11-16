package br.com.provasmart.api.dto.request.studyplan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Atualização da conclusão de uma atividade do plano")
public record StudyPlanActivityCompletionRequestDTO(

        @NotNull(message = "A situação de conclusão da atividade é obrigatória")
        @Schema(description = "Indica se a atividade está concluída", example = "true")
        Boolean completed
) {
}