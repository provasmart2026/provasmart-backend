package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {ISimulationQuestionMapper.class})
public interface ISimulationMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "status", expression = "java(SimulationStatusEnum.EM_ANDAMENTO)")
    @Mapping(target = "startedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "finishedAt", ignore = true)
    @Mapping(target = "questions", ignore = true)
    SimulationEntity toEntity(UUID studentId);

    SimulationResponseDTO toDTO(SimulationEntity entity);
}
