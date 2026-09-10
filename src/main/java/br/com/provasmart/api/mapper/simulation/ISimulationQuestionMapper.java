package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.dto.response.simulation.SimulationQuestionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ISimulationAlternativeMapper.class})
public interface ISimulationQuestionMapper {

    @Mapping(target = "questionId", source = "entity.question.id")
    @Mapping(target = "statement", source = "entity.question.statement")
    @Mapping(target = "alternatives", source = "entity.question.alternatives")
    @Mapping(target = "selectedAlternativeId", source = "entity.answer.selectedAlternative.id")
    SimulationQuestionResponseDTO toDTO(SimulationQuestionEntity entity);
}
