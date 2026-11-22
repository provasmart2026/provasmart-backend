package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.reports.QuestionReportEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.enums.*;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.dto.request.report.QuestionReportStatusRequestDTO;
import br.com.provasmart.api.mapper.report.IQuestionReportMapper;
import br.com.provasmart.api.repository.reports.IQuestionReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionReportQueryServiceTest {
    @Mock private IQuestionReportRepository reportRepository;
    @Spy private IQuestionReportMapper reportMapper = Mappers.getMapper(IQuestionReportMapper.class);
    @InjectMocks private QuestionReportService service;

    @Test
    void countsOnlyPendingReportsForTheAdmin() {
        when(reportRepository.countByStatus(QuestionReportStatusEnum.PENDENTE)).thenReturn(12L);

        assertEquals(12L, service.countPendingReports());

        verify(reportRepository).countByStatus(QuestionReportStatusEnum.PENDENTE);
        verifyNoMoreInteractions(reportRepository);
    }

    @Test
    void allowsReactivationBelowThePendingReporterLimit() {
        var questionId = UUID.randomUUID();
        when(reportRepository.countDistinctStudentsByQuestionAndStatus(questionId, QuestionReportStatusEnum.PENDENTE))
                .thenReturn(9L);

        assertDoesNotThrow(() -> service.validateReactivationAllowed(questionId));
    }

    @ParameterizedTest
    @ValueSource(longs = {10, 11})
    void blocksReactivationAtOrAboveThePendingReporterLimit(long pendingReporters) {
        var questionId = UUID.randomUUID();
        when(reportRepository.countDistinctStudentsByQuestionAndStatus(questionId, QuestionReportStatusEnum.PENDENTE))
                .thenReturn(pendingReporters);

        var error = assertThrows(ConflictException.class, () -> service.validateReactivationAllowed(questionId));

        assertEquals("Resolva ou descarte reportes até que a questão tenha menos de 10 pendências para reativá-la.",
                error.getMessage());
    }

    @Test
    void listsReportsWithoutStatusFilter() {
        var entity = report();
        var ordered = PageRequest.of(1, 10);
        when(reportRepository.findAllByOrderByCreatedAtDesc(ordered)).thenReturn(new PageImpl<>(List.of(entity), ordered, 11));
        var result = service.findReports(null, PageRequest.of(1, 10));
        assertEquals(11, result.getTotalElements());
        assertEquals(1, result.getNumber());
        assertEquals(entity.getOriginalQuestionId(), result.getContent().getFirst().originalQuestionId());
        assertEquals(entity.getStudent().getName(), result.getContent().getFirst().studentName());
        assertEquals(entity.getStudent().getId(), result.getContent().getFirst().studentId());
        verify(reportRepository).findAllByOrderByCreatedAtDesc(ordered);
        verify(reportRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(QuestionReportStatusEnum.class)
    void filtersByRequestedStatus(QuestionReportStatusEnum status) {
        var entity = report();
        entity.setStatus(status);
        var ordered = PageRequest.of(0, 10);
        when(reportRepository.findByStatusOrderByCreatedAtDesc(status, ordered)).thenReturn(new PageImpl<>(List.of(entity)));
        assertEquals(status, service.findReports(status, PageRequest.of(0, 10)).getContent().getFirst().status());
        verify(reportRepository, never()).findAllByOrderByCreatedAtDesc(any(org.springframework.data.domain.Pageable.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void returnsOriginalQuestionAndActivityIdentifiersAndSnapshot(boolean practice) {
        var entity = report();
        var activityId = UUID.randomUUID();
        var activityQuestionId = UUID.randomUUID();
        if (practice) {
            var activity = new DirectedPracticeEntity();
            activity.setId(activityId);
            var question = new DirectedPracticeQuestionEntity();
            question.setId(activityQuestionId);
            question.setPractice(activity);
            entity.setPracticeQuestion(question);
            entity.setOrigin(QuestionReportOriginEnum.PRATICA_DIRECIONADA);
        } else {
            var activity = new SimulationEntity();
            activity.setId(activityId);
            var question = new SimulationQuestionEntity();
            question.setId(activityQuestionId);
            question.setSimulation(activity);
            entity.setSimulationQuestion(question);
        }
        when(reportRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        var detail = service.findReportById(entity.getId());
        assertEquals(entity.getOriginalQuestionId(), detail.originalQuestionId());
        assertEquals(entity.getContentSnapshot(), detail.contentSnapshot());
        assertEquals(entity.getDescription(), detail.description());
        assertEquals(activityId, practice ? detail.practiceId() : detail.simulationId());
        assertEquals(activityQuestionId, practice ? detail.practiceQuestionId() : detail.simulationQuestionId());
        assertNull(practice ? detail.simulationId() : detail.practiceId());
        assertEquals(entity.getStudent().getName(), detail.studentName());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void rejectsMissingReport() {
        var id = UUID.randomUUID();
        when(reportRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findReportById(id));
    }

    @ParameterizedTest
    @EnumSource(value = QuestionReportStatusEnum.class, names = {"RESOLVIDO", "DESCARTADO"})
    void closesPendingReportAndPreservesItsOriginalData(QuestionReportStatusEnum target) {
        var entity = report();
        var createdAt = entity.getCreatedAt();
        var originalId = entity.getOriginalQuestionId();
        var snapshot = entity.getContentSnapshot();
        when(reportRepository.findByIdForUpdate(entity.getId())).thenReturn(Optional.of(entity));
        when(reportRepository.save(entity)).thenReturn(entity);
        var before = LocalDateTime.now();

        var result = service.updateStatus(entity.getId(), new QuestionReportStatusRequestDTO(target));

        assertEquals(target, result.status());
        assertFalse(result.updatedAt().isBefore(before));
        assertEquals(createdAt, result.createdAt());
        assertEquals(originalId, result.originalQuestionId());
        assertEquals(snapshot, result.contentSnapshot());
        verify(reportRepository).save(entity);
    }

    @ParameterizedTest
    @EnumSource(value = QuestionReportStatusEnum.class, names = {"RESOLVIDO", "DESCARTADO"})
    void rejectsAlreadyClosedReport(QuestionReportStatusEnum current) {
        var entity = report();
        entity.setStatus(current);
        when(reportRepository.findByIdForUpdate(entity.getId())).thenReturn(Optional.of(entity));
        assertThrows(ConflictException.class,
                () -> service.updateStatus(entity.getId(), new QuestionReportStatusRequestDTO(QuestionReportStatusEnum.RESOLVIDO)));
        assertEquals(current, entity.getStatus());
        assertNull(entity.getUpdatedAt());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void rejectsPendingOrMissingTargetStatusBeforeLoadingReport() {
        assertThrows(BadRequestException.class, () -> service.updateStatus(UUID.randomUUID(),
                new QuestionReportStatusRequestDTO(QuestionReportStatusEnum.PENDENTE)));
        assertThrows(BadRequestException.class, () -> service.updateStatus(UUID.randomUUID(),
                new QuestionReportStatusRequestDTO(null)));
        verifyNoInteractions(reportRepository);
    }

    @Test
    void rejectsStatusChangeForMissingReport() {
        var id = UUID.randomUUID();
        when(reportRepository.findByIdForUpdate(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateStatus(id,
                new QuestionReportStatusRequestDTO(QuestionReportStatusEnum.RESOLVIDO)));
        verify(reportRepository, never()).save(any());
    }

    private QuestionReportEntity report() {
        var student = new UserEntity();
        student.setId(UUID.randomUUID());
        student.setName("Estudante de teste");
        var entity = new QuestionReportEntity();
        entity.setId(UUID.randomUUID());
        entity.setStudent(student);
        entity.setOriginalQuestionId(UUID.randomUUID());
        entity.setOrigin(QuestionReportOriginEnum.SIMULADO);
        entity.setReason(QuestionReportReasonEnum.OUTRO);
        entity.setDescription("Descrição de teste");
        entity.setContentSnapshot("{\"statement\":\"Enunciado\",\"alternatives\":[]}");
        entity.setStatus(QuestionReportStatusEnum.PENDENTE);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
