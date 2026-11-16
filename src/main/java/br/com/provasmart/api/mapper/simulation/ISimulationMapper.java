package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationSummaryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ISimulationQuestionMapper.class})
public interface ISimulationMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "student")
    @Mapping(target = "status", expression = "java(SimulationStatusEnum.EM_ANDAMENTO)")
    @Mapping(target = "startedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "finishedAt", ignore = true)
    @Mapping(target = "questions", ignore = true)
    SimulationEntity toEntity(UserEntity student);

    @Mapping(target = "studentId", source = "student.id")
    SimulationResponseDTO toDTO(SimulationEntity entity);

    @Mapping(target = "id", source = "simulation.id")
    @Mapping(target = "status", source = "simulation.status")
    @Mapping(target = "startedAt", source = "simulation.startedAt")
    @Mapping(target = "finishedAt", source = "simulation.finishedAt")
    @Mapping(target = "totalQuestions", source = "totalQuestions")
    @Mapping(target = "answeredQuestions", source = "answeredQuestions")
    @Mapping(target = "correctAnswers", source = "correctAnswers")
    @Mapping(target = "incorrectAnswers", source = "incorrectAnswers")
    @Mapping(target = "accuracyPercentage", source = "accuracyPercentage")
    SimulationSummaryResponseDTO toSummaryDTO(SimulationEntity simulation, Integer totalQuestions,
                                             Integer answeredQuestions, Integer correctAnswers,
                                             Integer incorrectAnswers, Double accuracyPercentage);
}
