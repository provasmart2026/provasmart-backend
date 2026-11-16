package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.request.report.QuestionReportRequestDTO;
import br.com.provasmart.api.dto.request.report.QuestionReportStatusRequestDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportSummaryResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportDetailResponseDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.report.IQuestionReportMapper;
import br.com.provasmart.api.repository.practice.IDirectedPracticeRepository;
import br.com.provasmart.api.repository.reports.IQuestionReportRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.IQuestionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionReportService implements IQuestionReportService {
    private final ICurrentActorService currentActorService;
    private final IUserRepository userRepository;
    private final ISimulationRepository simulationRepository;
    private final IDirectedPracticeRepository practiceRepository;
    private final IQuestionReportRepository reportRepository;
    private final IQuestionReportMapper reportMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public QuestionReportResponseDTO reportSimulationQuestion(UUID simulationId, UUID simulationQuestionId,
                                                              QuestionReportRequestDTO request) {
        var student = currentStudentForUpdate();
        var simulation = simulationRepository.findByIdAndStudentIdForUpdate(simulationId, student.getId())
                .orElseThrow(() -> new NotFoundException("Simulado não encontrado."));
        if (simulation.getStatus() != SimulationStatusEnum.EM_ANDAMENTO) {
            throw new ConflictException("Só é possível reportar questões de um simulado em andamento.");
        }
        var question = simulation.getQuestions().stream()
                .filter(item -> item.getId().equals(simulationQuestionId)).findFirst()
                .orElseThrow(() -> new BadRequestException("A questão não pertence a este simulado."));
        var original = question.getQuestion();
        validateNoPendingReport(student.getId(), original.getId());
        var alternatives = original.getAlternatives().stream()
                .map(item -> new AlternativeSnapshot(item.getId(), item.getLetter(), item.getText()))
                .sorted(Comparator.comparing(AlternativeSnapshot::letter)).toList();
        var snapshot = objectMapper.writeValueAsString(new ContentSnapshot(original.getStatement(), alternatives));
        var report = reportMapper.toSimulationEntity(request, student, question, snapshot);
        return reportMapper.toResponseDTO(reportRepository.save(report));
    }

    @Override
    @Transactional
    public QuestionReportResponseDTO reportPracticeQuestion(UUID practiceId, UUID practiceQuestionId,
                                                            QuestionReportRequestDTO request) {
        var student = currentStudentForUpdate();
        var practice = practiceRepository.findByIdAndStudent_Id(practiceId, student.getId())
                .orElseThrow(() -> new NotFoundException("Prática direcionada não encontrada."));
        if (practice.getStatus() != DirectedPracticeStatus.EM_ANDAMENTO) {
            throw new ConflictException("Só é possível reportar questões de uma prática em andamento.");
        }
        var question = practice.getQuestions().stream()
                .filter(item -> item.getId().equals(practiceQuestionId)).findFirst()
                .orElseThrow(() -> new BadRequestException("A questão não pertence a esta prática."));
        validateNoPendingReport(student.getId(), question.getOriginalQuestionId());
        var alternatives = question.getAlternatives().stream()
                .map(item -> new AlternativeSnapshot(item.getId(), item.getLetter(), item.getText()))
                .sorted(Comparator.comparing(AlternativeSnapshot::letter)).toList();
        var snapshot = objectMapper.writeValueAsString(new ContentSnapshot(question.getStatement(), alternatives));
        var report = reportMapper.toPracticeEntity(request, student, question, snapshot);
        return reportMapper.toResponseDTO(reportRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionReportSummaryResponseDTO> findReports(QuestionReportStatusEnum status, Pageable pageable) {
        var reports = status == null ? reportRepository.findAllByOrderByCreatedAtDesc(pageable)
                : reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return reports.map(reportMapper::toSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionReportDetailResponseDTO findReportById(UUID id) {
        var report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reporte não encontrado."));
        return reportMapper.toDetailDTO(report);
    }

    @Override
    @Transactional
    public QuestionReportDetailResponseDTO updateStatus(UUID id, QuestionReportStatusRequestDTO request) {
        if (request.status() != QuestionReportStatusEnum.RESOLVIDO
                && request.status() != QuestionReportStatusEnum.DESCARTADO) {
            throw new BadRequestException("O novo status deve ser RESOLVIDO ou DESCARTADO.");
        }
        var report = reportRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Reporte não encontrado."));
        if (report.getStatus() != QuestionReportStatusEnum.PENDENTE) {
            throw new ConflictException("Somente reportes pendentes podem ser resolvidos ou descartados.");
        }
        report.setStatus(request.status());
        report.setUpdatedAt(LocalDateTime.now());
        return reportMapper.toDetailDTO(reportRepository.save(report));
    }

    private UserEntity currentStudentForUpdate() {
        return userRepository.findByIdForUpdate(currentActorService.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Estudante não encontrado."));
    }

    private void validateNoPendingReport(UUID studentId, UUID originalQuestionId) {
        if (reportRepository.existsByStudent_IdAndOriginalQuestionIdAndStatus(
                studentId, originalQuestionId, QuestionReportStatusEnum.PENDENTE)) {
            throw new ConflictException("Você já possui um reporte pendente para esta questão.");
        }
    }

    private record AlternativeSnapshot(UUID id, String letter, String text) {}

    private record ContentSnapshot(String statement, List<AlternativeSnapshot> alternatives) {}
}