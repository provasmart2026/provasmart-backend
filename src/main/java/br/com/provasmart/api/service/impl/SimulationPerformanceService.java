package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.simulation.performance.*;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.simulation.performance.ISimulationPerformanceMapper;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.ISimulationPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationPerformanceService implements ISimulationPerformanceService {

    private final ISimulationRepository simulationRepository;
    private final ICurrentActorService currentActorService;
    private final ISimulationPerformanceMapper performanceMapper;

    @Override
    @Transactional(readOnly = true)
    public SimulationPerformanceResponseDTO findById(UUID simulationId) {
        log.info("Fetching simulation performance: simulationId={}", simulationId);
        var studentId = currentActorService.getCurrentUserId();
        var simulation = getSimulation(simulationId, studentId);

        validateSimulation(simulation);

        var questions = simulation.getQuestions();
        var totalQuestions = questions.size();
        var correctAnswers = countCorrectAnswers(questions);
        var summary = buildSummary(totalQuestions, correctAnswers);
        var areas = buildAreas(questions);

        var worstAreas = findWorstAreas(areas);
        var response = buildResponse(simulation, summary, worstAreas, areas);

        log.info("Simulation performance calculated: simulationId={}, totalQuestions={}, worstAreas={}",
                simulation.getId(), totalQuestions, worstAreas);
        return response;
    }

    private SimulationPerformanceResponseDTO buildResponse(SimulationEntity simulation, SimulationPerformanceSummaryDTO summary,
                                                           List<ExamAreaEnum> worstAreas,
                                                           List<SimulationAreaPerformanceDTO> areas) {
        log.info("Building simulation performance response: simulationId={}, totalAreas={}, worstAreas={}",
                simulation.getId(), areas.size(), worstAreas);
        return performanceMapper.toResponseDTO(simulation.getId(), summary, worstAreas, areas);
    }

    private SimulationPerformanceSummaryDTO buildSummary(int totalQuestions, int correctAnswers) {
        log.info("Building performance summary");
        return performanceMapper.toSummaryDTO(totalQuestions, correctAnswers,
                totalQuestions - correctAnswers, accuracyPercentage(correctAnswers, totalQuestions));
    }

    private SimulationEntity getSimulation(UUID simulationId, UUID studentId) {
        log.info("Retrieving simulation: simulationId={}, studentId={}", simulationId, studentId);
        return simulationRepository.findByIdAndStudent_Id(simulationId, studentId)
                .orElseThrow(() -> new NotFoundException("Simulado não encontrado."));
    }

    private void validateSimulation(SimulationEntity simulation) {
        log.info("Validating simulation performance availability: simulationId={}, status={}", simulation.getId(), simulation.getStatus());
        if (simulation.getStatus() != SimulationStatusEnum.FINALIZADO) {
            throw new ConflictException("A análise está disponível apenas para simulados finalizados.");
        }

        var questions = simulation.getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new ConflictException("O simulado não possui questões para análise.");
        }

        for (var question : questions) {
            if (question.getExamAreaSnapshot() == null
                    || question.getDisciplineIdSnapshot() == null
                    || question.getDisciplineNameSnapshot() == null
                    || question.getDisciplineNameSnapshot().isBlank()
                    || question.getSubjectIdSnapshot() == null
                    || question.getSubjectNameSnapshot() == null
                    || question.getSubjectNameSnapshot().isBlank()) {
                throw new ConflictException("O simulado não possui classificação preservada suficiente para análise.");
            }
            if (question.getAnswer() == null || question.getAnswer().getIsCorrect() == null) {
                throw new ConflictException("O simulado possui questões sem correção registrada.");
            }
        }
    }

    private List<SimulationAreaPerformanceDTO> buildAreas(List<SimulationQuestionEntity> questions) {
        log.info("Calculating area performance: totalQuestions={}", questions.size());
        return questions.stream()
                .collect(Collectors.groupingBy(SimulationQuestionEntity::getExamAreaSnapshot))
                .entrySet().stream()
                .sorted(comparingByKey())
                .map(entry -> {
                    var areaQuestions = entry.getValue();
                    var totalQuestions = areaQuestions.size();
                    var correctAnswers = countCorrectAnswers(areaQuestions);
                    return performanceMapper.toAreaDTO(entry.getKey(), totalQuestions, correctAnswers,
                            totalQuestions - correctAnswers, accuracyPercentage(correctAnswers, totalQuestions),
                            buildDisciplines(entry.getKey(), areaQuestions));
                })
                .toList();
    }

    private List<SimulationDisciplinePerformanceDTO> buildDisciplines(ExamAreaEnum examArea, List<SimulationQuestionEntity> questions) {
        log.info("Calculating discipline performance: examArea={}, totalQuestions={}", examArea, questions.size());
        return questions.stream()
                .collect(Collectors.groupingBy(SimulationQuestionEntity::getDisciplineIdSnapshot))
                .entrySet().stream()
                .map(entry -> {
                    var disciplineQuestions = entry.getValue();
                    var totalQuestions = disciplineQuestions.size();
                    var correctAnswers = countCorrectAnswers(disciplineQuestions);
                    return performanceMapper.toDisciplineDTO(entry.getKey(),
                            disciplineQuestions.getFirst().getDisciplineNameSnapshot(),
                            totalQuestions, correctAnswers, totalQuestions - correctAnswers,
                            accuracyPercentage(correctAnswers, totalQuestions), buildSubjects(entry.getKey(), disciplineQuestions));
                })
                .sorted(Comparator.comparing(SimulationDisciplinePerformanceDTO::disciplineName)
                        .thenComparing(SimulationDisciplinePerformanceDTO::disciplineId))
                .toList();
    }

    private List<SimulationSubjectPerformanceDTO> buildSubjects(UUID disciplineId, List<SimulationQuestionEntity> questions) {
        log.info("Calculating subject performance: disciplineId={}, totalQuestions={}", disciplineId, questions.size());
        return questions.stream()
                .collect(Collectors.groupingBy(SimulationQuestionEntity::getSubjectIdSnapshot))
                .entrySet().stream()
                .map(entry -> {
                    var subjectQuestions = entry.getValue();
                    var totalQuestions = subjectQuestions.size();
                    var correctAnswers = countCorrectAnswers(subjectQuestions);
                    return performanceMapper.toSubjectDTO(entry.getKey(),
                            subjectQuestions.getFirst().getSubjectNameSnapshot(),
                            totalQuestions, correctAnswers, totalQuestions - correctAnswers,
                            accuracyPercentage(correctAnswers, totalQuestions));
                })
                .sorted(Comparator.comparing(SimulationSubjectPerformanceDTO::subjectName)
                        .thenComparing(SimulationSubjectPerformanceDTO::subjectId))
                .toList();
    }

    private int countCorrectAnswers(List<SimulationQuestionEntity> questions) {
        log.info("Counting correct answers: totalQuestions={}", questions.size());
        return (int) questions.stream()
                .filter(question -> Boolean.TRUE.equals(question.getAnswer().getIsCorrect()))
                .count();
    }

    private double accuracyPercentage(int correctAnswers, int totalQuestions) {
        log.info("Calculating accuracy percentage: correctAnswers={}, totalQuestions={}", correctAnswers, totalQuestions);
        return correctAnswers * 100.0 / totalQuestions;
    }

    private List<ExamAreaEnum> findWorstAreas(List<SimulationAreaPerformanceDTO> areas) {
        log.info("Identifying all lowest-performing areas: totalAreas={}", areas.size());
        var lowestCorrectAnswers = areas.stream()
                .mapToInt(SimulationAreaPerformanceDTO::correctAnswers)
                .min()
                .orElseThrow(() -> new ConflictException("O simulado não possui áreas para análise."));

        return areas.stream()
                .filter(area -> area.correctAnswers() == lowestCorrectAnswers)
                .map(SimulationAreaPerformanceDTO::examArea)
                .toList();
    }
}