package br.com.provasmart.api.mapper.simulation.performance;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.dto.response.simulation.performance.*;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ISimulationPerformanceMapper {

    SimulationPerformanceSummaryDTO toSummaryDTO(Integer totalQuestions, Integer correctAnswers,
                                                 Integer incorrectAnswers, Double accuracyPercentage);

    SimulationSubjectPerformanceDTO toSubjectDTO(UUID subjectId, String subjectName, Integer totalQuestions,
                                                 Integer correctAnswers, Integer incorrectAnswers,
                                                 Double accuracyPercentage);

    SimulationDisciplinePerformanceDTO toDisciplineDTO(UUID disciplineId, String disciplineName,
                                                       Integer totalQuestions, Integer correctAnswers,
                                                       Integer incorrectAnswers, Double accuracyPercentage,
                                                       List<SimulationSubjectPerformanceDTO> subjects);

    SimulationAreaPerformanceDTO toAreaDTO(ExamAreaEnum examArea, Integer totalQuestions, Integer correctAnswers,
                                           Integer incorrectAnswers, Double accuracyPercentage,
                                           List<SimulationDisciplinePerformanceDTO> disciplines);

    SimulationPerformanceResponseDTO toResponseDTO(UUID simulationId, SimulationPerformanceSummaryDTO summary,
                                                   List<ExamAreaEnum> worstAreas,
                                                   List<SimulationAreaPerformanceDTO> areas);
}