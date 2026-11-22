package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.report.QuestionReportRequestDTO;
import br.com.provasmart.api.dto.request.report.QuestionReportStatusRequestDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportSummaryResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportDetailResponseDTO;

import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IQuestionReportService {
    Page<QuestionReportSummaryResponseDTO> findReports(
            QuestionReportStatusEnum status,
            Pageable pageable);

    long countPendingReports();

    void validateReactivationAllowed(UUID questionId);

    QuestionReportDetailResponseDTO findReportById(UUID id);

    QuestionReportDetailResponseDTO updateStatus(UUID id, QuestionReportStatusRequestDTO request);

    QuestionReportResponseDTO reportSimulationQuestion(UUID simulationId, UUID simulationQuestionId,
                                                       QuestionReportRequestDTO request);

    QuestionReportResponseDTO reportPracticeQuestion(UUID practiceId, UUID practiceQuestionId,
                                                     QuestionReportRequestDTO request);
}
