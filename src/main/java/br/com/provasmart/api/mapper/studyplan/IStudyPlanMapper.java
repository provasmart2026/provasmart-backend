package br.com.provasmart.api.mapper.studyplan;

import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.studyplans.StudyPlanEntity;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanResponseDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanSummaryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {IStudyPlanActivityMapper.class})
public interface IStudyPlanMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "simulation.student")
    @Mapping(target = "simulation", source = "simulation")
    @Mapping(target = "dailyHours", source = "dailyHours")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "status", constant = "ATIVO")
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "activities", ignore = true)
    StudyPlanEntity toEntity(SimulationEntity simulation, Integer dailyHours);

    @Mapping(target = "simulationId", source = "entity.simulation.id")
    StudyPlanResponseDTO toDTO(StudyPlanEntity entity, Integer totalActivities,
                               Integer completedActivities, Double progressPercentage);

    @Mapping(target = "simulationId", source = "entity.simulation.id")
    StudyPlanSummaryResponseDTO toSummaryDTO(StudyPlanEntity entity, Integer totalActivities,
                                             Integer completedActivities, Double progressPercentage);
}
