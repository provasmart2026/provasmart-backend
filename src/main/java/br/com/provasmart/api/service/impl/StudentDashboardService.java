package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.dashboard.DashboardAreaPerformanceDTO;
import br.com.provasmart.api.dto.response.dashboard.DashboardEvolutionPointDTO;
import br.com.provasmart.api.dto.response.dashboard.StudentDashboardResponseDTO;
import br.com.provasmart.api.repository.practice.IDirectedPracticeRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.IStudentDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentDashboardService implements IStudentDashboardService {

    private static final int EVOLUTION_LIMIT = 10;

    private final ISimulationRepository simulationRepository;
    private final IDirectedPracticeRepository directedPracticeRepository;
    private final ICurrentActorService currentActorService;

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponseDTO getSimulationDashboard() {
        var studentId = currentActorService.getCurrentUserId();
        log.info("Fetching simulation dashboard.");
        var simulations = findCompletedSimulations(studentId);
        var response = buildSimulationDashboard(simulations);
        log.info("Simulation dashboard calculated.");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponseDTO getDirectedPracticeDashboard() {
        var studentId = currentActorService.getCurrentUserId();
        log.info("Fetching directed practice dashboard.");
        var practices = findCompletedPractices(studentId);
        var response = buildDirectedPracticeDashboard(practices);
        log.info("Directed practice dashboard calculated.");
        return response;
    }

    private List<DirectedPracticeEntity> findCompletedPractices(UUID studentId) {
        log.info("Loading completed directed practices.");
        return directedPracticeRepository.findAllByStudent_IdAndStatus(studentId, DirectedPracticeStatus.FINALIZADA);
    }

    private StudentDashboardResponseDTO buildDirectedPracticeDashboard(List<DirectedPracticeEntity> practices) {
        log.info("Grouping completed directed practices by area.");
        var practicesByArea = practices.stream()
                .collect(Collectors.groupingBy(DirectedPracticeEntity::getArea,
                        () -> new EnumMap<>(ExamAreaEnum.class), Collectors.toList()));
        var areas = practicesByArea.entrySet().stream()
                .map(entry -> buildPracticeAreaPerformance(entry.getKey(), entry.getValue()))
                .toList();

        var totalQuestions = areas.stream().mapToInt(DashboardAreaPerformanceDTO::totalQuestions).sum();
        var correctAnswers = areas.stream().mapToInt(DashboardAreaPerformanceDTO::correctAnswers).sum();
        return new StudentDashboardResponseDTO(practices.size(), totalQuestions, correctAnswers,
                totalQuestions - correctAnswers, calculateAccuracyPercentage(correctAnswers, totalQuestions), areas,
                buildPracticeEvolution(practices));
    }

    private DashboardAreaPerformanceDTO buildPracticeAreaPerformance(ExamAreaEnum area,
                                                                    List<DirectedPracticeEntity> practices) {
        log.info("Counting directed practice answers.");
        var correctedQuestions = practices.stream()
                .flatMap(practice -> findCorrectedPracticeQuestions(practice).stream())
                .toList();
        var correctAnswers = countCorrectPracticeAnswers(correctedQuestions);
        return buildAreaPerformance(area, correctedQuestions.size(), correctAnswers);
    }

    private List<DashboardEvolutionPointDTO> buildPracticeEvolution(List<DirectedPracticeEntity> practices) {
        var points = practices.stream()
                .filter(practice -> practice.getFinishedAt() != null)
                .sorted(Comparator.comparing(DirectedPracticeEntity::getFinishedAt))
                .map(this::buildPracticeEvolutionPoint)
                .filter(point -> point.totalQuestions() > 0)
                .toList();
        return points.subList(Math.max(0, points.size() - EVOLUTION_LIMIT), points.size());
    }

    private DashboardEvolutionPointDTO buildPracticeEvolutionPoint(DirectedPracticeEntity practice) {
        var correctedQuestions = findCorrectedPracticeQuestions(practice);
        var totalQuestions = correctedQuestions.size();
        var correctAnswers = countCorrectPracticeAnswers(correctedQuestions);
        return new DashboardEvolutionPointDTO(practice.getFinishedAt(), totalQuestions, correctAnswers,
                calculateAccuracyPercentage(correctAnswers, totalQuestions));
    }

    private List<DirectedPracticeQuestionEntity> findCorrectedPracticeQuestions(DirectedPracticeEntity practice) {
        return practice.getQuestions().stream()
                .filter(question -> question.getCorrectAtFinish() != null)
                .toList();
    }

    private int countCorrectPracticeAnswers(List<DirectedPracticeQuestionEntity> questions) {
        return Math.toIntExact(questions.stream()
                .map(DirectedPracticeQuestionEntity::getCorrectAtFinish)
                .filter(Boolean.TRUE::equals)
                .count());
    }

    private List<SimulationEntity> findCompletedSimulations(UUID studentId) {
        log.info("Loading completed simulations.");
        return simulationRepository.findAllByStudent_IdAndStatus(studentId, SimulationStatusEnum.FINALIZADO);
    }

    private StudentDashboardResponseDTO buildSimulationDashboard(List<SimulationEntity> simulations) {
        log.info("Building simulation dashboard response.");
        var correctedQuestions = simulations.stream()
                .flatMap(simulation -> findCorrectedSimulationQuestions(simulation).stream())
                .toList();

        var totalQuestions = correctedQuestions.size();
        var correctAnswers = countCorrectAnswers(correctedQuestions);
        return new StudentDashboardResponseDTO(simulations.size(), totalQuestions,
                correctAnswers, totalQuestions - correctAnswers,
                calculateAccuracyPercentage(correctAnswers, totalQuestions), buildAreaPerformances(correctedQuestions),
                buildSimulationEvolution(simulations));
    }

    private List<DashboardEvolutionPointDTO> buildSimulationEvolution(List<SimulationEntity> simulations) {
        var points = simulations.stream()
                .filter(simulation -> simulation.getFinishedAt() != null)
                .sorted(Comparator.comparing(SimulationEntity::getFinishedAt))
                .map(this::buildSimulationEvolutionPoint)
                .filter(point -> point.totalQuestions() > 0)
                .toList();
        return points.subList(Math.max(0, points.size() - EVOLUTION_LIMIT), points.size());
    }

    private DashboardEvolutionPointDTO buildSimulationEvolutionPoint(SimulationEntity simulation) {
        var correctedQuestions = findCorrectedSimulationQuestions(simulation);
        var totalQuestions = correctedQuestions.size();
        var correctAnswers = countCorrectAnswers(correctedQuestions);
        return new DashboardEvolutionPointDTO(simulation.getFinishedAt(), totalQuestions, correctAnswers,
                calculateAccuracyPercentage(correctAnswers, totalQuestions));
    }

    private List<SimulationQuestionEntity> findCorrectedSimulationQuestions(SimulationEntity simulation) {
        return simulation.getQuestions().stream()
                .filter(question -> question.getAnswer() != null && question.getAnswer().getIsCorrect() != null)
                .toList();
    }

    private List<DashboardAreaPerformanceDTO> buildAreaPerformances(List<SimulationQuestionEntity> questions) {
        log.info("Grouping corrected simulation questions by area.");
        var questionsByArea = questions.stream()
                .filter(question -> question.getExamAreaSnapshot() != null)
                .collect(Collectors.groupingBy(SimulationQuestionEntity::getExamAreaSnapshot,
                        () -> new EnumMap<>(ExamAreaEnum.class), Collectors.toList()));

        return questionsByArea.entrySet().stream()
                .map(entry -> buildAreaPerformance(entry.getKey(), entry.getValue()))
                .toList();
    }

    private DashboardAreaPerformanceDTO buildAreaPerformance(ExamAreaEnum area, List<SimulationQuestionEntity> questions) {
        var totalQuestions = questions.size();
        log.info("Building simulation area performance.");
        var correctAnswers = countCorrectAnswers(questions);
        return buildAreaPerformance(area, totalQuestions, correctAnswers);
    }

    private DashboardAreaPerformanceDTO buildAreaPerformance(ExamAreaEnum area, int totalQuestions, int correctAnswers) {
        log.info("Building dashboard area performance.");
        var percentage = calculateAccuracyPercentage(correctAnswers, totalQuestions);
        return new DashboardAreaPerformanceDTO(area, totalQuestions, correctAnswers,
                totalQuestions - correctAnswers, percentage, classify(percentage));
    }

    private int countCorrectAnswers(List<SimulationQuestionEntity> questions) {
        log.info("Counting correct simulation answers.");
        return Math.toIntExact(questions.stream()
                .filter(question -> Boolean.TRUE.equals(question.getAnswer().getIsCorrect()))
                .count());
    }

    private double calculateAccuracyPercentage(int correctAnswers, int totalQuestions) {
        log.info("Calculating accuracy.");
        return totalQuestions == 0 ? 0.0 : correctAnswers * 100.0 / totalQuestions;
    }

    private String classify(double percentage) {
        log.info("Classifying area performance.");
        if (percentage < 50) {
            return "Dificuldade alta";
        }
        if (percentage < 70) {
            return "Dificuldade média";
        }
        return "Desempenho satisfatório";
    }
}
