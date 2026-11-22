package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.studyplans.StudyPlanActivityEntity;
import br.com.provasmart.api.domain.entity.studyplans.StudyPlanEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.domain.enums.StudyPlanActivityTypeEnum;
import br.com.provasmart.api.domain.enums.StudyPlanStatusEnum;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanActivityCompletionRequestDTO;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanCreateRequestDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationAreaPerformanceDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationDisciplinePerformanceDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationPerformanceResponseDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationPerformanceSummaryDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationSubjectPerformanceDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanActivityResponseDTO;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.studyplan.IStudyPlanActivityMapper;
import br.com.provasmart.api.mapper.studyplan.IStudyPlanMapper;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.studyplans.IStudyPlanRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.ISimulationPerformanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyPlanServiceTest {

    @Mock private ICurrentActorService actor;
    @Mock private ISimulationRepository simulations;
    @Mock private IStudyPlanRepository plans;
    @Mock private ISimulationPerformanceService performance;
    @Spy private IStudyPlanMapper planMapper = Mappers.getMapper(IStudyPlanMapper.class);
    @Spy private IStudyPlanActivityMapper activityMapper = Mappers.getMapper(IStudyPlanActivityMapper.class);
    @InjectMocks private StudyPlanService service;

    private final UUID studentId = UUID.randomUUID();
    private final UUID simulationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(planMapper, "iStudyPlanActivityMapper", activityMapper);
        when(actor.getCurrentUserId()).thenReturn(studentId);
    }

    @Test
    void buildsASevenDayPlanPrioritizingWeakAreasAndSubjectsWithErrors() {
        allowLatestSimulation();
        allowPerformance(area(ExamAreaEnum.CIENCIAS_HUMANAS, subject("História", 10, 3)),
                area(ExamAreaEnum.MATEMATICA, subject("Funções", 10, 4)),
                area(ExamAreaEnum.LINGUAGENS, subject("Vocabulário", 3, 2), subject("Fonética", 3, 2),
                        subject("Gramática", 4, 4), subject("Literatura", 2, 0)));
        when(plans.save(any(StudyPlanEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new StudyPlanCreateRequestDTO(simulationId, 2, false));

        assertEquals(simulationId, result.simulationId());
        assertEquals(StudyPlanStatusEnum.ATIVO, result.status());
        assertEquals(14, result.totalActivities());
        assertEquals(0, result.completedActivities());
        assertEquals(0.0, result.progressPercentage());
        var activities = result.activities();
        assertEquals(List.of("Gramática", "Fonética", "Vocabulário", "Funções", "História"),
                activities.subList(0, 5).stream().map(StudyPlanActivityResponseDTO::subjectName).toList());
        for (int index = 0; index < activities.size(); index++) {
            var activity = activities.get(index);
            assertEquals(index / 2 + 1, activity.dayNumber());
            assertEquals(index % 2 + 1, activity.position());
            assertEquals(index < 5 ? StudyPlanActivityTypeEnum.ESTUDO : StudyPlanActivityTypeEnum.REVISAO,
                    activity.type());
        }
        assertEquals("Gramática", activities.get(5).subjectName());
        verify(plans, never()).saveAndFlush(any());
    }

    @Test
    void archivesTheActivePlanBeforeSavingItsConfirmedReplacement() {
        allowLatestSimulation();
        allowPerformance(area(ExamAreaEnum.MATEMATICA, subject("Funções", 10, 4)));
        var active = plan(StudyPlanStatusEnum.ATIVO);
        when(plans.findByStudent_IdAndStatus(studentId, StudyPlanStatusEnum.ATIVO)).thenReturn(Optional.of(active));
        when(plans.save(any(StudyPlanEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new StudyPlanCreateRequestDTO(simulationId, 1, true));

        assertEquals(StudyPlanStatusEnum.ARQUIVADO, active.getStatus());
        assertNotNull(active.getArchivedAt());
        assertEquals(StudyPlanStatusEnum.ATIVO, result.status());
        var order = inOrder(plans);
        order.verify(plans).saveAndFlush(active);
        order.verify(plans).save(argThat(newPlan -> newPlan != active && newPlan.getStatus() == StudyPlanStatusEnum.ATIVO));
    }

    @Test
    void requiresConfirmationBeforeReplacingTheActivePlan() {
        allowLatestSimulation();
        allowPerformance(area(ExamAreaEnum.MATEMATICA, subject("Funções", 10, 4)));
        var active = plan(StudyPlanStatusEnum.ATIVO);
        when(plans.findByStudent_IdAndStatus(studentId, StudyPlanStatusEnum.ATIVO)).thenReturn(Optional.of(active));

        var error = assertThrows(ConflictException.class,
                () -> service.create(new StudyPlanCreateRequestDTO(simulationId, 1, false)));

        assertEquals("Confirme a substituição do plano ativo para gerar um novo.", error.getMessage());
        assertEquals(StudyPlanStatusEnum.ATIVO, active.getStatus());
        assertNull(active.getArchivedAt());
        verify(plans, never()).saveAndFlush(any());
        verify(plans, never()).save(any());
    }

    @Test
    void requiresAFinishedSimulationBeforeGeneratingAPlan() {
        when(simulations.findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(
                studentId, SimulationStatusEnum.FINALIZADO)).thenReturn(Optional.empty());

        var error = assertThrows(ConflictException.class,
                () -> service.create(new StudyPlanCreateRequestDTO(simulationId, 1, false)));

        assertEquals("Finalize um Simulado Geral para gerar seu plano de estudos.", error.getMessage());
        verifyNoInteractions(plans, performance);
    }

    @Test
    void rejectsARequestForAnOlderSimulation() {
        allowLatestSimulation();

        var error = assertThrows(ConflictException.class,
                () -> service.create(new StudyPlanCreateRequestDTO(UUID.randomUUID(), 1, false)));

        assertEquals("O simulado de referência mudou. Atualize a tela antes de gerar o plano.", error.getMessage());
        verifyNoInteractions(plans, performance);
    }

    @Test
    void rejectsASecondPlanForTheSameSimulation() {
        allowLatestSimulation();
        when(plans.existsBySimulation_Id(simulationId)).thenReturn(true);

        var error = assertThrows(ConflictException.class,
                () -> service.create(new StudyPlanCreateRequestDTO(simulationId, 1, true)));

        assertTrue(error.getMessage().startsWith("Este simulado já possui um plano de estudos."));
        verifyNoInteractions(performance);
        verify(plans, never()).save(any());
    }

    @Test
    void rejectsAPlanWhenTheSimulationHasNoErrors() {
        allowLatestSimulation();
        allowPerformance(area(ExamAreaEnum.MATEMATICA, subject("Funções", 10, 0)));

        var error = assertThrows(ConflictException.class,
                () -> service.create(new StudyPlanCreateRequestDTO(simulationId, 1, false)));

        assertTrue(error.getMessage().startsWith("Não identificamos assuntos com dificuldade"));
        verify(plans, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void findsTheActivePlanOnlyWhenOneExists(boolean exists) {
        var active = plan(StudyPlanStatusEnum.ATIVO);
        when(plans.findByStudent_IdAndStatus(studentId, StudyPlanStatusEnum.ATIVO))
                .thenReturn(exists ? Optional.of(active) : Optional.empty());

        var result = service.findCurrent();

        if (exists) assertEquals(active.getId(), result.orElseThrow().id());
        else assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void listsTheStudentsPlansWithOptionalStatusAndPreservesPagination(boolean filtered) {
        var archived = plan(StudyPlanStatusEnum.ARQUIVADO);
        archived.getActivities().addAll(List.of(activity(true), activity(false)));
        var pageable = PageRequest.of(1, 2);
        var page = new PageImpl<>(List.of(archived), pageable, 3);
        if (filtered) {
            when(plans.findByStudent_IdAndStatusOrderByCreatedAtDesc(
                    studentId, StudyPlanStatusEnum.ARQUIVADO, pageable)).thenReturn(page);
        } else {
            when(plans.findByStudent_IdOrderByCreatedAtDesc(studentId, pageable)).thenReturn(page);
        }

        var result = service.findAll(filtered ? StudyPlanStatusEnum.ARQUIVADO : null, pageable);

        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getNumber());
        var summary = result.getContent().getFirst();
        assertEquals(archived.getId(), summary.id());
        assertEquals(1, summary.completedActivities());
        assertEquals(50.0, summary.progressPercentage());
    }

    @Test
    void findsAnArchivedPlanForTheCurrentStudent() {
        var archived = plan(StudyPlanStatusEnum.ARQUIVADO);
        when(plans.findByIdAndStudent_Id(archived.getId(), studentId)).thenReturn(Optional.of(archived));

        var result = service.findById(archived.getId());

        assertEquals(archived.getId(), result.id());
        assertEquals(StudyPlanStatusEnum.ARQUIVADO, result.status());
        assertEquals(0.0, result.progressPercentage());
    }

    @Test
    void refusesToReadAPlanNotOwnedByTheCurrentStudent() {
        var id = UUID.randomUUID();
        when(plans.findByIdAndStudent_Id(id, studentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @ParameterizedTest
    @CsvSource({"false,true", "true,false", "true,true"})
    void updatesCompletionAndKeepsTheOriginalTimeOnRepeatedCompletion(boolean before, boolean completed) {
        var active = plan(StudyPlanStatusEnum.ATIVO);
        var activity = activity(before);
        var originalTime = activity.getCompletedAt();
        active.getActivities().add(activity);
        when(plans.findByIdAndStudent_Id(active.getId(), studentId)).thenReturn(Optional.of(active));
        when(plans.save(active)).thenReturn(active);

        var result = service.updateActivityCompletion(active.getId(), activity.getId(),
                new StudyPlanActivityCompletionRequestDTO(completed));

        assertEquals(completed, result.activities().getFirst().completed());
        assertEquals(completed ? 100.0 : 0.0, result.progressPercentage());
        if (!completed) assertNull(activity.getCompletedAt());
        else if (before) assertEquals(originalTime, activity.getCompletedAt());
        else assertNotNull(activity.getCompletedAt());
        verify(plans).save(active);
    }

    @Test
    void refusesToUpdateAnActivityInAnArchivedPlan() {
        var archived = plan(StudyPlanStatusEnum.ARQUIVADO);
        when(plans.findByIdAndStudent_Id(archived.getId(), studentId)).thenReturn(Optional.of(archived));

        var error = assertThrows(ConflictException.class, () -> service.updateActivityCompletion(
                archived.getId(), UUID.randomUUID(), new StudyPlanActivityCompletionRequestDTO(true)));

        assertEquals("Planos arquivados estão disponíveis somente para consulta.", error.getMessage());
        verify(plans, never()).save(any());
    }

    @Test
    void refusesToUpdateAnActivityThatDoesNotBelongToThePlan() {
        var active = plan(StudyPlanStatusEnum.ATIVO);
        active.getActivities().add(activity(false));
        when(plans.findByIdAndStudent_Id(active.getId(), studentId)).thenReturn(Optional.of(active));

        var error = assertThrows(NotFoundException.class, () -> service.updateActivityCompletion(
                active.getId(), UUID.randomUUID(), new StudyPlanActivityCompletionRequestDTO(true)));

        assertEquals("Atividade não encontrada neste plano.", error.getMessage());
        verify(plans, never()).save(any());
    }

    private void allowLatestSimulation() {
        when(simulations.findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(
                studentId, SimulationStatusEnum.FINALIZADO)).thenReturn(Optional.of(finishedSimulation()));
    }

    private SimulationEntity finishedSimulation() {
        var student = new UserEntity();
        student.setId(studentId);
        var simulation = new SimulationEntity();
        simulation.setId(simulationId);
        simulation.setStudent(student);
        simulation.setStatus(SimulationStatusEnum.FINALIZADO);
        return simulation;
    }

    private StudyPlanEntity plan(StudyPlanStatusEnum status) {
        var plan = new StudyPlanEntity();
        plan.setId(UUID.randomUUID());
        plan.setSimulation(finishedSimulation());
        plan.setStatus(status);
        plan.setDailyHours(1);
        plan.setStartDate(LocalDate.of(2026, 11, 20));
        plan.setCreatedAt(plan.getStartDate().atStartOfDay());
        return plan;
    }

    private StudyPlanActivityEntity activity(boolean completed) {
        var activity = new StudyPlanActivityEntity();
        activity.setId(UUID.randomUUID());
        activity.setCompleted(completed);
        activity.setCompletedAt(completed ? LocalDateTime.of(2026, 11, 20, 12, 0) : null);
        return activity;
    }

    private void allowPerformance(SimulationAreaPerformanceDTO... areas) {
        var list = List.of(areas);
        var total = list.stream().mapToInt(SimulationAreaPerformanceDTO::totalQuestions).sum();
        var errors = list.stream().mapToInt(SimulationAreaPerformanceDTO::incorrectAnswers).sum();
        var summary = new SimulationPerformanceSummaryDTO(total, total - errors, errors, 100.0 * (total - errors) / total);
        when(performance.findById(simulationId)).thenReturn(
                new SimulationPerformanceResponseDTO(simulationId, summary, List.of(), list));
    }

    private SimulationAreaPerformanceDTO area(ExamAreaEnum area, SimulationSubjectPerformanceDTO... subjects) {
        var list = List.of(subjects);
        var total = list.stream().mapToInt(SimulationSubjectPerformanceDTO::totalQuestions).sum();
        var errors = list.stream().mapToInt(SimulationSubjectPerformanceDTO::incorrectAnswers).sum();
        var correct = total - errors;
        double accuracy = 100.0 * correct / total;
        var discipline = new SimulationDisciplinePerformanceDTO(UUID.randomUUID(), area.name(), total, correct,
                errors, accuracy, list);
        return new SimulationAreaPerformanceDTO(area, total, correct, errors, accuracy, List.of(discipline));
    }

    private SimulationSubjectPerformanceDTO subject(String name, int total, int errors) {
        var correct = total - errors;
        return new SimulationSubjectPerformanceDTO(UUID.randomUUID(), name, total, correct, errors, 100.0 * correct / total);
    }
}
