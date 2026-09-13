package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationAnswerEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ISimulationAnswerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "simulationQuestion", source = "simulationQuestion")
    @Mapping(target = "selectedAlternative", source = "alternative")
    @Mapping(target = "answeredAt", expression = "java(java.time.LocalDateTime.now())")
    void toEntity(SimulationQuestionEntity simulationQuestion, AlternativeEntity alternative,
                  @MappingTarget SimulationAnswerEntity answer);
}
