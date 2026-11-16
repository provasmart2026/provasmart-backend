package br.com.provasmart.api.dto.request.report;

import br.com.provasmart.api.domain.enums.QuestionReportReasonEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para reportar um problema em uma questão durante a atividade")
public record QuestionReportRequestDTO(
        @NotNull(message = "Informe o motivo do reporte")
        @Schema(description = "Motivo do problema encontrado", example = "ALTERNATIVAS_COM_PROBLEMA")
        QuestionReportReasonEnum reason,

        @NotBlank(message = "Descreva o problema encontrado na questão")
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        @Schema(description = "Descrição do problema", example = "As alternativas A e B possuem o mesmo texto.")
        String description
) {
}