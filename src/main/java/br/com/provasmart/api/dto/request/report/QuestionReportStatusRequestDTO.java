package br.com.provasmart.api.dto.request.report;

import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import jakarta.validation.constraints.NotNull;

public record QuestionReportStatusRequestDTO(
        @NotNull(message = "Informe o novo status do reporte")
        QuestionReportStatusEnum status
) {
}