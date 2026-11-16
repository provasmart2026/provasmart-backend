package br.com.provasmart.api.dto.response.report;

import br.com.provasmart.api.domain.enums.QuestionReportOriginEnum;
import br.com.provasmart.api.domain.enums.QuestionReportReasonEnum;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Confirmação do registro de um reporte de questão")
public record QuestionReportResponseDTO(
        UUID id,
        QuestionReportOriginEnum origin,
        QuestionReportReasonEnum reason,
        String description,
        QuestionReportStatusEnum status,
        LocalDateTime createdAt
) {
}