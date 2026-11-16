package br.com.provasmart.api.dto.response.report;

import br.com.provasmart.api.domain.enums.QuestionReportOriginEnum;
import br.com.provasmart.api.domain.enums.QuestionReportReasonEnum;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuestionReportDetailResponseDTO(
        UUID id,
        UUID studentId,
        String studentName,
        UUID originalQuestionId,
        QuestionReportOriginEnum origin,
        UUID simulationId,
        UUID simulationQuestionId,
        UUID practiceId,
        UUID practiceQuestionId,
        QuestionReportReasonEnum reason,
        String description,
        String contentSnapshot,
        QuestionReportStatusEnum status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}