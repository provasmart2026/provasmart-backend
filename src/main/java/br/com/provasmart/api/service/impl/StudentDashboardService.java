package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.dashboard.DashboardAreaPerformanceDTO;
import br.com.provasmart.api.dto.response.dashboard.StudentDashboardResponseDTO;
import br.com.provasmart.api.repository.practice.IDirectedPracticeRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.IStudentDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentDashboardService implements IStudentDashboardService {

    private final ISimulationRepository simulationRepository;
    private final IDirectedPracticeRepository directedPracticeRepository;
    private final ICurrentActorService currentActorService;

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponseDTO getSimulationDashboard() {
        var studentId = currentActorService.getCurrentUserId();
        log.info("Fetching simulation dashboard: studentId={}", studentId);
        var simulations = findCompletedSimulations(studentId);
        var response = buildSimulationDashboard(simulations);
        log.info("Simulation dashboard calculated: completedActivities={}, totalQuestions={}, totalAreas={}",
                response.completedActivities(), response.totalQuestions(), response.areas().size());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponseDTO getDirectedPracticeDashboard() {
        var studentId = currentActorService.getCurrentUserId();
        log.info("Fetching directed practice dashboard: studentId={}", studentId);
        var practices = findCompletedPractices(studentId);
        var response = buildDirectedPracticeDashboard(practices);
        log.info("Directed practice dashboard calculated: completedActivities={}, totalQuestions={}, totalAreas={}",
                response.completedActivities(), response.totalQuestions(), response.areas().size());
        return response;
    }

    private List<DirectedPracticeEntity> findCompletedPractices(UUID studentId) {
        log.info("Loading completed directed practices: studentId={}", studentId);
        return directedPracticeRepository.findAllByStudent_IdAndStatus(studentId, DirectedPracticeStatus.FINALIZADA);
    }

    private StudentDashboardResponseDTO buildDirectedPracticeDashboard(List<DirectedPracticeEntity> practices) {
        log.info("Grouping completed directed practices by area: completedActivities={}", practices.size());
        var practicesByArea = practices.stream()
                .collect(Collectors.groupingBy(DirectedPracticeEntity::getArea,
                        () -> new EnumMap<>(ExamAreaEnum.class), Collectors.toList()));
        var areas = practicesByArea.entrySet().stream()
                .map(entry -> buildPracticeAreaPerformance(entry.getKey(), entry.getValue()))
                .toList();

        var totalQuestions = areas.stream().mapToInt(DashboardAreaPerformanceDTO::totalQuestions).sum();
        var correctAnswers = areas.stream().mapToInt(DashboardAreaPerformanceDTO::correctAnswers).sum();
        return new StudentDashboardResponseDTO(practices.size(), totalQuestions, correctAnswers,
                totalQuestions - correctAnswers, calculateAccuracyPercentage(correctAnswers, totalQuestions), areas);
    }

    private DashboardAreaPerformanceDTO buildPracticeAreaPerformance(ExamAreaEnum area,
                                                                    List<DirectedPracticeEntity> practices) {
        log.info("Counting directed practice answers: examArea={}, completedActivities={}", area, practices.size());
        var correctedQuestions = practices.stream()
                .flatMap(practice -> practice.getQuestions().stream())
                .filter(question -> question.getCorrectAtFinish() != null)
                .toList();
        var correctAnswers = Math.toIntExact(correctedQuestions.stream()
                .map(DirectedPracticeQuestionEntity::getCorrectAtFinish)
                .filter(Boolean.TRUE::equals)
                .count());
        return buildAreaPerformance(area, correctedQuestions.size(), correctAnswers);
    }

    private List<SimulationEntity> findCompletedSimulations(UUID studentId) {
        log.info("Loading completed simulations: studentId={}", studentId);
        return simulationRepository.findAllByStudent_IdAndStatus(studentId, SimulationStatusEnum.FINALIZADO);
    }

    private StudentDashboardResponseDTO buildSimulationDashboard(List<SimulationEntity> simulations) {
        log.info("Building simulation dashboard response: completedActivities={}", simulations.size());
        var correctedQuestions = simulations.stream()
                .flatMap(simulation -> simulation.getQuestions().stream())
                .filter(question -> question.getAnswer() != null && question.getAnswer().getIsCorrect() != null)
                .toList();

        var totalQuestions = correctedQuestions.size();
        var correctAnswers = countCorrectAnswers(correctedQuestions);
        return new StudentDashboardResponseDTO(simulations.size(), totalQuestions,
                correctAnswers, totalQuestions - correctAnswers,
                calculateAccuracyPercentage(correctAnswers, totalQuestions), buildAreaPerformances(correctedQuestions));
    }

    private List<DashboardAreaPerformanceDTO> buildAreaPerformances(List<SimulationQuestionEntity> questions) {
        log.info("Grouping corrected simulation questions by area: totalQuestions={}", questions.size());
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
        log.info("Building simulation area performance: examArea={}, totalQuestions={}", area, totalQuestions);
        var correctAnswers = countCorrectAnswers(questions);
        return buildAreaPerformance(area, totalQuestions, correctAnswers);
    }

    private DashboardAreaPerformanceDTO buildAreaPerformance(ExamAreaEnum area, int totalQuestions, int correctAnswers) {
        log.info("Building dashboard area performance: examArea={}, totalQuestions={}", area, totalQuestions);
        var percentage = calculateAccuracyPercentage(correctAnswers, totalQuestions);
        return new DashboardAreaPerformanceDTO(area, totalQuestions, correctAnswers,
                totalQuestions - correctAnswers, percentage, classify(percentage));
    }

    private int countCorrectAnswers(List<SimulationQuestionEntity> questions) {
        log.info("Counting correct simulation answers: totalQuestions={}", questions.size());
        return Math.toIntExact(questions.stream()
                .filter(question -> Boolean.TRUE.equals(question.getAnswer().getIsCorrect()))
                .count());
    }

    private double calculateAccuracyPercentage(int correctAnswers, int totalQuestions) {
        log.info("Calculating accuracy: correctAnswers={}, totalQuestions={}", correctAnswers, totalQuestions);
        return totalQuestions == 0 ? 0.0 : correctAnswers * 100.0 / totalQuestions;
    }

    private String classify(double percentage) {
        log.info("Classifying area performance: accuracyPercentage={}", percentage);
        if (percentage < 50) {
            return "Dificuldade alta";
        }
        if (percentage < 70) {
            return "Dificuldade média";
        }
        return "Desempenho satisfatório";
    }
}
