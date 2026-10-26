package br.com.provasmart.api.mapper.studyplan;

import br.com.provasmart.api.domain.entity.studyplans.StudyPlanActivityEntity;
import br.com.provasmart.api.domain.entity.studyplans.StudyPlanEntity;
import br.com.provasmart.api.domain.enums.StudyPlanActivityTypeEnum;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationAreaPerformanceDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationDisciplinePerformanceDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationSubjectPerformanceDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanActivityResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IStudyPlanActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studyPlan", source = "plan")
    @Mapping(target = "dayNumber", source = "dayNumber")
    @Mapping(target = "position", source = "position")
    @Mapping(target = "examArea", source = "area.examArea")
    @Mapping(target = "disciplineIdSnapshot", source = "discipline.disciplineId")
    @Mapping(target = "disciplineNameSnapshot", source = "discipline.disciplineName")
    @Mapping(target = "subjectIdSnapshot", source = "subject.subjectId")
    @Mapping(target = "subjectNameSnapshot", source = "subject.subjectName")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "completedAt", ignore = true)
    StudyPlanActivityEntity toEntity(StudyPlanEntity plan,Integer dayNumber,Integer position,SimulationAreaPerformanceDTO area,
            SimulationDisciplinePerformanceDTO discipline,SimulationSubjectPerformanceDTO subject,StudyPlanActivityTypeEnum type);

    @Mapping(target = "disciplineId", source = "disciplineIdSnapshot")
    @Mapping(target = "disciplineName", source = "disciplineNameSnapshot")
    @Mapping(target = "subjectId", source = "subjectIdSnapshot")
    @Mapping(target = "subjectName", source = "subjectNameSnapshot")
    StudyPlanActivityResponseDTO toDTO(StudyPlanActivityEntity entity);
}
