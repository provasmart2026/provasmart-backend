package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.dto.response.simulation.SimulationQuestionCorrectionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ISimulationAlternativeMapper.class)
public interface ISimulationQuestionCorrectionMapper {

    @Mapping(target = "simulationQuestionId", source = "id")
    @Mapping(target = "statement", source = "question.statement")
    @Mapping(target = "alternatives", source = "question.alternatives")
    @Mapping(target = "selectedAlternativeId", source = "answer.selectedAlternative.id")
    @Mapping(target = "isCorrect", source = "answer.isCorrect")
    @Mapping(target = "explanation", source = "question.explanation")
    SimulationQuestionCorrectionResponseDTO toDTO(SimulationQuestionEntity entity);
}
