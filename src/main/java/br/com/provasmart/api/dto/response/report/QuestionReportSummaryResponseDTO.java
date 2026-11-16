package br.com.provasmart.api.dto.response.report;

import br.com.provasmart.api.domain.enums.QuestionReportOriginEnum;
import br.com.provasmart.api.domain.enums.QuestionReportReasonEnum;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuestionReportSummaryResponseDTO(
        UUID id,
        UUID studentId,
        String studentName,
        UUID originalQuestionId,
        QuestionReportOriginEnum origin,
        QuestionReportReasonEnum reason,
        QuestionReportStatusEnum status,
        LocalDateTime createdAt
) {
}