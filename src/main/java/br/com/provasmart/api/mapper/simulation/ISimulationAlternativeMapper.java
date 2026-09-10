package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.dto.response.simulation.SimulationAlternativeResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ISimulationAlternativeMapper {

    SimulationAlternativeResponseDTO toDTO(AlternativeEntity entity);
}
