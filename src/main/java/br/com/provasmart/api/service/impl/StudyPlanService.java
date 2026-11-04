package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.studyplans.StudyPlanActivityEntity;
import br.com.provasmart.api.domain.entity.studyplans.StudyPlanEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.domain.enums.StudyPlanActivityTypeEnum;
import br.com.provasmart.api.domain.enums.StudyPlanStatusEnum;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanActivityCompletionRequestDTO;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanCreateRequestDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationAreaPerformanceDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationDisciplinePerformanceDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationSubjectPerformanceDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationPerformanceResponseDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanResponseDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanSummaryResponseDTO;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.studyplan.IStudyPlanActivityMapper;
import br.com.provasmart.api.mapper.studyplan.IStudyPlanMapper;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.studyplans.IStudyPlanRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.ISimulationPerformanceService;
import br.com.provasmart.api.service.IStudyPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyPlanService implements IStudyPlanService {

    private final ICurrentActorService currentActorService;
    private final ISimulationRepository simulationRepository;
    private final IStudyPlanRepository studyPlanRepository;
    private final ISimulationPerformanceService simulationPerformanceService;
    private final IStudyPlanMapper studyPlanMapper;
    private final IStudyPlanActivityMapper studyPlanActivityMapper;

    @Override
    @Transactional
    public StudyPlanResponseDTO create(StudyPlanCreateRequestDTO requestDTO) {
        log.info("Creating study plan");
        var studentId = getStudentId();
        var simulation = getLastSimulation(studentId);

        validateSimulationReference(requestDTO.simulationId(), simulation);
        validateSimulationHasNoStudyPlan(simulation.getId());

        var performance = getSimulationPerformance(simulation.getId());
        validateSimulationHasErrors(performance);

        var activePlan = getActivePlan(studentId);
        validatePlanReplacement(activePlan, requestDTO.replaceCurrentPlan());

        var subjects = getSubjectsWithErrors(performance);
        sortSubjects(subjects);

        var plan = buildPlan(simulation, requestDTO.dailyHours());
        distributeActivities(plan, subjects);

        archiveActivePlan(activePlan);
        var savedPlan = savePlan(plan);
        log.info("Study plan save requested; transaction commit pending");

        return toDTO(savedPlan);
    }

    @Override
    public Optional<StudyPlanResponseDTO> findCurrent() {
        log.info("Fetching current study plan");
        var studentId = getStudentId();

        return getActivePlan(studentId)
                .map(this::toDTO);
    }

    @Override
    public Page<StudyPlanSummaryResponseDTO> findAll(StudyPlanStatusEnum status, Pageable pageable) {
        log.info("Fetching study plan history");
        var studentId = getStudentId();

        var plans = status == null
                ? studyPlanRepository.findByStudent_IdOrderByCreatedAtDesc(studentId, pageable)
                : studyPlanRepository.findByStudent_IdAndStatusOrderByCreatedAtDesc(studentId, status, pageable);

        return plans.map(this::toSummaryDTO);
    }

    @Override
    public StudyPlanResponseDTO findById(UUID studyPlanId) {
        log.info("Fetching study plan details");
        var plan = getStudentPlan(studyPlanId);

        return toDTO(plan);
    }

    @Override
    @Transactional
    public StudyPlanResponseDTO updateActivityCompletion(UUID studyPlanId, UUID activityId,
                                                         StudyPlanActivityCompletionRequestDTO requestDTO) {
        log.info("Updating study plan activity completion");
        var plan = getStudentPlan(studyPlanId);
        validatePlanIsActive(plan);

        var activity = getPlanActivity(plan, activityId);
        updateCompletion(activity, requestDTO.completed());

        var savedPlan = savePlan(plan);
        return toDTO(savedPlan);
    }

    private void updateCompletion(StudyPlanActivityEntity activity, Boolean completed) {
        log.info("Updating activity completion state");

        if (!completed.equals(activity.getCompleted())) {
            activity.setCompleted(completed);
            activity.setCompletedAt(completed ? LocalDateTime.now() : null);
        }
    }

    private StudyPlanActivityEntity getPlanActivity(StudyPlanEntity plan, UUID activityId) {
        log.info("Fetching study plan activity");

        return plan.getActivities().stream()
                .filter(activity -> activity.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada neste plano."));
    }

    private void validatePlanIsActive(StudyPlanEntity plan) {
        log.info("Validating study plan is active");
        if (plan.getStatus() != StudyPlanStatusEnum.ATIVO) {
            throw new ConflictException(
                    "Planos arquivados estão disponíveis somente para consulta.");
        }
    }

    private StudyPlanEntity getStudentPlan(UUID studyPlanId) {
        log.info("Fetching student study plan");
        var studentId = getStudentId();

        return studyPlanRepository.findByIdAndStudent_Id(studyPlanId, studentId)
                .orElseThrow(() -> new NotFoundException("Plano de estudos não encontrado."));
    }

    private StudyPlanSummaryResponseDTO toSummaryDTO(StudyPlanEntity plan) {
        log.info("Mapping study plan summary");
        var total = plan.getActivities().size();

        var completed = (int) plan.getActivities().stream()
                .filter(activity -> Boolean.TRUE.equals(activity.getCompleted()))
                .count();

        var progress = total == 0 ? 0.0 : completed * 100.0 / total;

        return studyPlanMapper.toSummaryDTO(plan, total, completed, progress);
    }

    private StudyPlanResponseDTO toDTO(StudyPlanEntity plan) {
        log.info("Mapping study plan response");
        var total = plan.getActivities().size();

        var completed = (int) plan.getActivities().stream()
                .filter(activity -> Boolean.TRUE.equals(activity.getCompleted()))
                .count();

        var progress = total == 0 ? 0.0 : completed * 100.0 / total;

        return studyPlanMapper.toDTO(plan, total, completed, progress);
    }

    private StudyPlanEntity savePlan(StudyPlanEntity plan) {
        log.info("Saving study plan");
        return studyPlanRepository.save(plan);
    }

    private void archiveActivePlan(Optional<StudyPlanEntity> activePlan) {
        log.info("Checking active plan for archival");
        activePlan.ifPresent(plan -> {
            plan.setStatus(StudyPlanStatusEnum.ARQUIVADO);
            plan.setArchivedAt(LocalDateTime.now());
            studyPlanRepository.saveAndFlush(plan);
        });
    }

    private void distributeActivities(StudyPlanEntity plan, List<StudySubject> subjects) {
        log.info("Distributing study plan activities");
        var totalActivities = 7 * plan.getDailyHours();

        for (var i = 0; i < totalActivities; i++) {
            var item = subjects.get(i % subjects.size());
            var day = i / plan.getDailyHours() + 1;
            var position = i % plan.getDailyHours() + 1;

            var type = i < subjects.size() ? StudyPlanActivityTypeEnum.ESTUDO : StudyPlanActivityTypeEnum.REVISAO;

            var activity = studyPlanActivityMapper.toEntity(
                    plan, day, position,
                    item.area(), item.discipline(), item.subject(), type);

            plan.getActivities().add(activity);
        }
    }

    private StudyPlanEntity buildPlan(SimulationEntity simulation, Integer dailyHours) {
        log.info("Building study plan");
        return studyPlanMapper.toEntity(simulation, dailyHours);
    }

    private int getAreaPriority(double accuracyPercentage) {
        log.info("Determining study area priority");
        if (accuracyPercentage < 50) {
            return 0;
        }
        if (accuracyPercentage < 70) {
            return 1;
        }
        return 2;
    }

    private void sortSubjects(List<StudySubject> subjects) {
        log.info("Sorting study subjects");
        subjects.sort(
                Comparator.comparingInt(
                                (StudySubject item) -> getAreaPriority(item.area().accuracyPercentage()))
                        .thenComparing(
                                Comparator.comparingInt(
                                                (StudySubject item) -> item.subject().incorrectAnswers())
                                        .reversed())
                        .thenComparing(
                                item -> item.subject().subjectName(),
                                String.CASE_INSENSITIVE_ORDER)
        );
    }

    private List<StudySubject> getSubjectsWithErrors(SimulationPerformanceResponseDTO performance) {
        log.info("Selecting study subjects");
        var subjects = new ArrayList<StudySubject>();

        for (var area : performance.areas()) {
            for (var discipline : area.disciplines()) {
                for (var subject : discipline.subjects()) {
                    if (subject.incorrectAnswers() > 0) {
                        subjects.add(new StudySubject(area, discipline, subject));
                    }
                }
            }
        }

        return subjects;
    }

    private Optional<StudyPlanEntity> getActivePlan(UUID studentId) {
        log.info("Fetching active study plan");
        return studyPlanRepository.findByStudent_IdAndStatus(studentId, StudyPlanStatusEnum.ATIVO);
    }

    private void validatePlanReplacement(Optional<StudyPlanEntity> activePlan, Boolean replaceCurrentPlan) {
        log.info("Validating study plan replacement confirmation");
        if (activePlan.isPresent() && !Boolean.TRUE.equals(replaceCurrentPlan)) {
            throw new ConflictException(
                    "Confirme a substituição do plano ativo para gerar um novo.");
        }
    }

    private void validateSimulationHasErrors(SimulationPerformanceResponseDTO performance) {
        log.info("Validating simulation eligibility for study plan");
        if (performance.summary().incorrectAnswers() == 0) {
            throw new ConflictException(
                    "Não identificamos assuntos com dificuldade para revisão neste simulado. "
                            + "Realize um novo Simulado Geral para verificar se há "
                            + "assuntos que precisam de reforço.");
        }
    }

    private SimulationPerformanceResponseDTO getSimulationPerformance(UUID simulationId) {
        log.info("Fetching simulation analysis for study plan");
        return simulationPerformanceService.findById(simulationId);
    }

    private void validateSimulationHasNoStudyPlan(UUID simulationId) {
        log.info("Checking whether simulation already has a study plan");
        if (studyPlanRepository.existsBySimulation_Id(simulationId)) {
            throw new ConflictException(
                    "Este simulado já possui um plano de estudos. "
                            + "Realize um novo Simulado Geral para gerar outro plano.");
        }
    }

    private void validateSimulationReference(UUID simulationId, SimulationEntity simulation) {
        log.info("Validating study plan simulation reference");
        if (!simulation.getId().equals(simulationId)) {
            throw new ConflictException(
                    "O simulado de referência mudou. Atualize a tela antes de gerar o plano.");
        }
    }

    private SimulationEntity getLastSimulation(UUID studentId) {
        log.info("Fetching latest finished simulation");
        return simulationRepository.findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(
                        studentId, SimulationStatusEnum.FINALIZADO)
                .orElseThrow(() -> new ConflictException(
                        "Finalize um Simulado Geral para gerar seu plano de estudos."));
    }

    private UUID getStudentId() {
        log.info("Resolving authenticated student");
        return currentActorService.getCurrentUserId();
    }

    private record StudySubject(
            SimulationAreaPerformanceDTO area,
            SimulationDisciplinePerformanceDTO discipline,
            SimulationSubjectPerformanceDTO subject) {
    }
}