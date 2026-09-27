package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
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
}
