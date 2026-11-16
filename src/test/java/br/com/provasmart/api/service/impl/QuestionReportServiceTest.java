package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeAlternativeEntity;
import br.com.provasmart.api.domain.entity.reports.QuestionReportEntity;
import br.com.provasmart.api.domain.enums.*;
import br.com.provasmart.api.dto.request.report.QuestionReportRequestDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.report.IQuestionReportMapper;
import br.com.provasmart.api.repository.practice.IDirectedPracticeRepository;
import br.com.provasmart.api.repository.reports.IQuestionReportRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionReportServiceTest {
    @Mock private ICurrentActorService currentActorService;
    @Mock private IUserRepository userRepository;
    @Mock private ISimulationRepository simulationRepository;
    @Mock private IDirectedPracticeRepository practiceRepository;
    @Mock private IQuestionReportRepository reportRepository;
    @Spy private IQuestionReportMapper reportMapper = Mappers.getMapper(IQuestionReportMapper.class);
    @Spy private ObjectMapper objectMapper = JsonMapper.builder().build();
    @InjectMocks private QuestionReportService service;

    private final UUID studentId = UUID.randomUUID();
    private final UUID activityId = UUID.randomUUID();
    private final UUID questionId = UUID.randomUUID();
    private final UUID originalId = UUID.randomUUID();
    private final QuestionReportRequestDTO request = new QuestionReportRequestDTO(
            QuestionReportReasonEnum.ALTERNATIVAS_COM_PROBLEMA, "Alternativas duplicadas.");

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void createsReportWithCorrectOriginAndPreservedContent(boolean practice) {
        var student = authenticate();
        if (practice) allowPractice(DirectedPracticeStatus.EM_ANDAMENTO);
        else allowSimulation(SimulationStatusEnum.EM_ANDAMENTO);
        var reportId = UUID.randomUUID();
        when(reportRepository.save(any())).thenAnswer(invocation -> {
            QuestionReportEntity report = invocation.getArgument(0);
            report.setId(reportId);
            return report;
        });

        var result = practice
                ? service.reportPracticeQuestion(activityId, questionId, request)
                : service.reportSimulationQuestion(activityId, questionId, request);

        var captor = ArgumentCaptor.forClass(QuestionReportEntity.class);
        verify(reportRepository).save(captor.capture());
        var saved = captor.getValue();
        assertSame(student, saved.getStudent());
        assertEquals(originalId, saved.getOriginalQuestionId());
        assertEquals(reportId, result.id());
        assertEquals(practice ? QuestionReportOriginEnum.PRATICA_DIRECIONADA : QuestionReportOriginEnum.SIMULADO,
                result.origin());
        assertEquals(QuestionReportStatusEnum.PENDENTE, result.status());
        assertEquals(request.reason(), result.reason());
        assertEquals(request.description(), result.description());
        assertNotNull(result.createdAt());
        assertNull(saved.getUpdatedAt());
        assertEquals(practice, saved.getPracticeQuestion() != null);
        assertEquals(!practice, saved.getSimulationQuestion() != null);
        var snapshot = objectMapper.readTree(saved.getContentSnapshot());
        assertEquals(practice ? "Enunciado preservado da prática" : "Enunciado do simulado", snapshot.get("statement").asText());
        assertEquals("A", snapshot.get("alternatives").get(0).get("letter").asText());
        assertEquals("Alternativa A", snapshot.get("alternatives").get(0).get("text").asText());
        assertFalse(snapshot.has("explanation"));
        assertFalse(snapshot.get("alternatives").get(0).has("correct"));
        verify(reportRepository).existsByStudent_IdAndOriginalQuestionIdAndStatus(studentId, originalId, QuestionReportStatusEnum.PENDENTE);
    }

    @ParameterizedTest
    @EnumSource(value = SimulationStatusEnum.class, names = {"FINALIZADO", "CANCELADO"})
    void rejectsClosedSimulation(SimulationStatusEnum status) {
        authenticate();
        allowSimulation(status);
        assertThrows(ConflictException.class, () -> service.reportSimulationQuestion(activityId, questionId, request));
        verifyNoInteractions(reportRepository);
    }

    @ParameterizedTest
    @EnumSource(value = DirectedPracticeStatus.class, names = {"FINALIZADA", "CANCELADA"})
    void rejectsClosedPractice(DirectedPracticeStatus status) {
        authenticate();
        allowPractice(status);
        assertThrows(ConflictException.class, () -> service.reportPracticeQuestion(activityId, questionId, request));
        verifyNoInteractions(reportRepository);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsMissingOrOtherStudentsActivity(boolean practice) {
        authenticate();
        if (practice) {
            when(practiceRepository.findByIdAndStudent_Id(activityId, studentId)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> service.reportPracticeQuestion(activityId, questionId, request));
        } else {
            when(simulationRepository.findByIdAndStudentIdForUpdate(activityId, studentId)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> service.reportSimulationQuestion(activityId, questionId, request));
        }
        verifyNoInteractions(reportRepository);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsQuestionOutsideTheActivity(boolean practice) {
        authenticate();
        if (practice) {
            allowPractice(DirectedPracticeStatus.EM_ANDAMENTO);
            assertThrows(BadRequestException.class, () -> service.reportPracticeQuestion(activityId, UUID.randomUUID(), request));
        } else {
            allowSimulation(SimulationStatusEnum.EM_ANDAMENTO);
            assertThrows(BadRequestException.class, () -> service.reportSimulationQuestion(activityId, UUID.randomUUID(), request));
        }
        verifyNoInteractions(reportRepository);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsPendingReportForOriginalQuestionRegardlessOfOrigin(boolean practice) {
        authenticate();
        when(reportRepository.existsByStudent_IdAndOriginalQuestionIdAndStatus(studentId, originalId, QuestionReportStatusEnum.PENDENTE))
                .thenReturn(true);
        if (practice) {
            allowPractice(DirectedPracticeStatus.EM_ANDAMENTO);
            assertThrows(ConflictException.class, () -> service.reportPracticeQuestion(activityId, questionId, request));
        } else {
            allowSimulation(SimulationStatusEnum.EM_ANDAMENTO);
            assertThrows(ConflictException.class, () -> service.reportSimulationQuestion(activityId, questionId, request));
        }
        verify(reportRepository, never()).save(any());
        verifyNoInteractions(reportMapper, objectMapper);
    }

    @Test
    void rejectsMissingStudentBeforeLoadingActivity() {
        when(currentActorService.getCurrentUserId()).thenReturn(studentId);
        when(userRepository.findByIdForUpdate(studentId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.reportSimulationQuestion(activityId, questionId, request));
        verifyNoInteractions(simulationRepository, practiceRepository, reportRepository);
    }

    @Test
    void validatesReasonAndDescriptionInRequest() {
        try (var factory = jakarta.validation.Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new QuestionReportRequestDTO(QuestionReportReasonEnum.OUTRO, "a".repeat(1000))).isEmpty());
            assertFalse(validator.validate(new QuestionReportRequestDTO(null, "Descrição")).isEmpty());
            assertFalse(validator.validate(new QuestionReportRequestDTO(QuestionReportReasonEnum.OUTRO, null)).isEmpty());
            assertFalse(validator.validate(new QuestionReportRequestDTO(QuestionReportReasonEnum.OUTRO, " \t\n")).isEmpty());
            assertFalse(validator.validate(new QuestionReportRequestDTO(QuestionReportReasonEnum.OUTRO, "a".repeat(1001))).isEmpty());
        }
    }

    private UserEntity authenticate() {
        var student = new UserEntity();
        student.setId(studentId);
        when(currentActorService.getCurrentUserId()).thenReturn(studentId);
        when(userRepository.findByIdForUpdate(studentId)).thenReturn(Optional.of(student));
        return student;
    }

    private void allowSimulation(SimulationStatusEnum status) {
        var alternative = new AlternativeEntity();
        alternative.setId(UUID.randomUUID());
        alternative.setLetter("A");
        alternative.setText("Alternativa A");
        alternative.setCorrect(true);
        var original = new QuestionEntity();
        original.setId(originalId);
        original.setStatement("Enunciado do simulado");
        original.setExplanation("Explicação não exibida durante a atividade");
        original.setAlternatives(List.of(alternative));
        var question = new SimulationQuestionEntity();
        question.setId(questionId);
        question.setQuestion(original);
        var simulation = new SimulationEntity();
        simulation.setId(activityId);
        simulation.setStatus(status);
        simulation.setQuestions(List.of(question));
        when(simulationRepository.findByIdAndStudentIdForUpdate(activityId, studentId)).thenReturn(Optional.of(simulation));
    }

    private void allowPractice(DirectedPracticeStatus status) {
        var alternative = new DirectedPracticeAlternativeEntity();
        alternative.setId(UUID.randomUUID());
        alternative.setLetter("A");
        alternative.setText("Alternativa A");
        alternative.setCorrect(true);
        var question = new DirectedPracticeQuestionEntity();
        question.setId(questionId);
        question.setOriginalQuestionId(originalId);
        question.setStatement("Enunciado preservado da prática");
        question.setAlternatives(List.of(alternative));
        var practice = new DirectedPracticeEntity();
        practice.setId(activityId);
        practice.setStatus(status);
        practice.setQuestions(List.of(question));
        when(practiceRepository.findByIdAndStudent_Id(activityId, studentId)).thenReturn(Optional.of(practice));
    }
}