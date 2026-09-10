package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ISimulationQuestionMapper.class})
public interface ISimulationMapper {

    SimulationResponseDTO toDTO(SimulationEntity entity);
}
