package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.reports.QuestionReportEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    public QuestionReportResponseDTO reportSimulationQuestion(
            UUID simulationId, UUID simulationQuestionId, QuestionReportRequestDTO request) {
        log.info("Reporting question {} from simulation {}", simulationQuestionId, simulationId);

        var student = currentStudentForUpdate();
        var simulation = findStudentSimulationForUpdate(simulationId, student.getId());
        validateSimulationReportable(simulation);

        var question = findSimulationQuestion(simulation, simulationQuestionId);
        var originalQuestion = question.getQuestion();
        validateNoPendingReport(student.getId(), originalQuestion.getId());

        var snapshot = buildSimulationQuestionSnapshot(originalQuestion);
        var report = reportMapper.toSimulationEntity(request, student, question, snapshot);
        return reportMapper.toResponseDTO(reportRepository.save(report));
    }

    @Override
    @Transactional
    public QuestionReportResponseDTO reportPracticeQuestion(
            UUID practiceId, UUID practiceQuestionId, QuestionReportRequestDTO request) {
        log.info("Reporting question {} from directed practice {}", practiceQuestionId, practiceId);

        var student = currentStudentForUpdate();
        var practice = findStudentPractice(practiceId, student.getId());
        validatePracticeReportable(practice);

        var question = findPracticeQuestion(practice, practiceQuestionId);
        validateNoPendingReport(student.getId(), question.getOriginalQuestionId());

        var snapshot = buildPracticeQuestionSnapshot(question);
        var report = reportMapper.toPracticeEntity(request, student, question, snapshot);
        return reportMapper.toResponseDTO(reportRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionReportSummaryResponseDTO> findReports(
            QuestionReportStatusEnum status, Pageable pageable) {
        log.info("Fetching question reports with status {}, page {}",
                status == null ? "all" : status.name(),
                pageable.isPaged() ? String.valueOf(pageable.getPageNumber()) : "unpaged");

        var reports = status == null
                ? reportRepository.findAllByOrderByCreatedAtDesc(pageable)
                : reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);

        return reports.map(reportMapper::toSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionReportDetailResponseDTO findReportById(UUID id) {
        log.info("Fetching question report with ID {}", id);

        var report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reporte não encontrado."));

        return reportMapper.toDetailDTO(report);
    }

    @Override
    @Transactional
    public QuestionReportDetailResponseDTO updateStatus(
            UUID id, QuestionReportStatusRequestDTO request) {
        var newStatus = request.status();
        log.info("Updating question report {} to status {}", id, newStatus);

        validateResolutionStatus(newStatus);

        var report = reportRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Reporte não encontrado."));
        validateReportPending(report);

        report.setStatus(newStatus);
        report.setUpdatedAt(LocalDateTime.now());

        return reportMapper.toDetailDTO(reportRepository.save(report));
    }

    private void validateResolutionStatus(QuestionReportStatusEnum status) {
        if (status != QuestionReportStatusEnum.RESOLVIDO && status != QuestionReportStatusEnum.DESCARTADO) {
            log.warn("Invalid question report resolution status {}", status);
            throw new BadRequestException("O novo status deve ser RESOLVIDO ou DESCARTADO.");
        }
    }

    private void validateReportPending(QuestionReportEntity report) {
        var status = report.getStatus();
        if (status != QuestionReportStatusEnum.PENDENTE) {
            log.warn("Question report {} cannot be updated from status {}", report.getId(), status);
            throw new ConflictException("Somente reportes pendentes podem ser resolvidos ou descartados.");
        }
    }

    private SimulationEntity findStudentSimulationForUpdate(UUID simulationId, UUID studentId) {
        log.info("Fetching simulation with lock for ID {}", simulationId);
        return simulationRepository.findByIdAndStudentIdForUpdate(simulationId, studentId)
                .orElseThrow(() -> new NotFoundException("Simulado não encontrado."));
    }

    private void validateSimulationReportable(SimulationEntity simulation) {
        var status = simulation.getStatus();
        if (status != SimulationStatusEnum.EM_ANDAMENTO && status != SimulationStatusEnum.FINALIZADO) {
            log.warn("Question report rejected for simulation {} with status {}", simulation.getId(), status);
            throw new ConflictException("Só é possível reportar questões de um simulado em andamento ou finalizado.");
        }
    }

    private SimulationQuestionEntity findSimulationQuestion(SimulationEntity simulation, UUID questionId) {
        log.info("Finding question {} in simulation {}", questionId, simulation.getId());
        return simulation.getQuestions().stream()
                .filter(item -> item.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("A questão não pertence a este simulado."));
    }

    private String buildSimulationQuestionSnapshot(QuestionEntity question) {
        log.debug("Building report snapshot for question {}", question.getId());
        var alternatives = question.getAlternatives().stream()
                .map(item -> new AlternativeSnapshot(item.getId(), item.getLetter(), item.getText()))
                .sorted(Comparator.comparing(AlternativeSnapshot::letter)).toList();
        return objectMapper.writeValueAsString(new ContentSnapshot(question.getStatement(), alternatives));
    }

    private DirectedPracticeEntity findStudentPractice(UUID practiceId, UUID studentId) {
        log.info("Fetching directed practice with ID {}", practiceId);
        return practiceRepository.findByIdAndStudent_Id(practiceId, studentId)
                .orElseThrow(() -> new NotFoundException("Prática direcionada não encontrada."));
    }

    private void validatePracticeReportable(DirectedPracticeEntity practice) {
        var status = practice.getStatus();
        if (status != DirectedPracticeStatus.EM_ANDAMENTO && status != DirectedPracticeStatus.FINALIZADA) {
            log.warn("Question report rejected for directed practice {} with status {}", practice.getId(), status);
            throw new ConflictException("Só é possível reportar questões de uma prática em andamento ou finalizada.");
        }
    }

    private DirectedPracticeQuestionEntity findPracticeQuestion(DirectedPracticeEntity practice, UUID questionId) {
        log.info("Finding question {} in directed practice {}", questionId, practice.getId());
        return practice.getQuestions().stream()
                .filter(item -> item.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("A questão não pertence a esta prática."));
    }

    private String buildPracticeQuestionSnapshot(DirectedPracticeQuestionEntity question) {
        log.debug("Building report snapshot for directed practice question {}", question.getId());
        var alternatives = question.getAlternatives().stream()
                .map(item -> new AlternativeSnapshot(item.getId(), item.getLetter(), item.getText()))
                .sorted(Comparator.comparing(AlternativeSnapshot::letter)).toList();
        return objectMapper.writeValueAsString(new ContentSnapshot(question.getStatement(), alternatives));
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
