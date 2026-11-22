package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.request.report.QuestionReportRequestDTO;
import br.com.provasmart.api.dto.request.report.QuestionReportStatusRequestDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportSummaryResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportDetailResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportPendingCountResponseDTO;
import br.com.provasmart.api.service.IQuestionReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class QuestionReportController {
    private final IQuestionReportService questionReportService;

    @PatchMapping("/question-reports/{id}/status")
    public ResponseEntity<QuestionReportDetailResponseDTO> updateStatus(
            @PathVariable UUID id, @RequestBody @Valid QuestionReportStatusRequestDTO request) {
        return ResponseEntity.ok(questionReportService.updateStatus(id, request));
    }

    @GetMapping("/question-reports")
    public ResponseEntity<Page<QuestionReportSummaryResponseDTO>> findReports(
            @RequestParam(required = false) QuestionReportStatusEnum status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(questionReportService.findReports(status, pageable));
    }

    @GetMapping("/question-reports/pending-count")
    public ResponseEntity<QuestionReportPendingCountResponseDTO> countPendingReports() {
        var count = questionReportService.countPendingReports();
        return ResponseEntity.ok(new QuestionReportPendingCountResponseDTO(count));
    }

    @GetMapping("/question-reports/{id}")
    public ResponseEntity<QuestionReportDetailResponseDTO> findReportById(@PathVariable UUID id) {
        return ResponseEntity.ok(questionReportService.findReportById(id));
    }

    @PostMapping("/simulations/{simulationId}/questions/{simulationQuestionId}/reports")
    public ResponseEntity<QuestionReportResponseDTO> reportSimulationQuestion(
            @PathVariable UUID simulationId,
            @PathVariable UUID simulationQuestionId,
            @RequestBody @Valid QuestionReportRequestDTO request) {
        var report = questionReportService.reportSimulationQuestion(simulationId, simulationQuestionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @PostMapping("/directed-practices/{practiceId}/questions/{practiceQuestionId}/reports")
    public ResponseEntity<QuestionReportResponseDTO> reportPracticeQuestion(
            @PathVariable UUID practiceId,
            @PathVariable UUID practiceQuestionId,
            @RequestBody @Valid QuestionReportRequestDTO request) {
        var report = questionReportService.reportPracticeQuestion(practiceId, practiceQuestionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
}