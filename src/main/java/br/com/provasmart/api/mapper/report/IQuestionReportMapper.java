package br.com.provasmart.api.mapper.report;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.reports.QuestionReportEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.dto.request.report.QuestionReportRequestDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportSummaryResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportDetailResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IQuestionReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "student")
    @Mapping(target = "originalQuestionId", source = "simulationQuestion.question.id")
    @Mapping(target = "origin", constant = "SIMULADO")
    @Mapping(target = "simulationQuestion", source = "simulationQuestion")
    @Mapping(target = "practiceQuestion", ignore = true)
    @Mapping(target = "reason", source = "request.reason")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "contentSnapshot", source = "contentSnapshot")
    @Mapping(target = "status", constant = "PENDENTE")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    QuestionReportEntity toSimulationEntity(QuestionReportRequestDTO request, UserEntity student,
                                            SimulationQuestionEntity simulationQuestion, String contentSnapshot);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "student")
    @Mapping(target = "originalQuestionId", source = "practiceQuestion.originalQuestionId")
    @Mapping(target = "origin", constant = "PRATICA_DIRECIONADA")
    @Mapping(target = "simulationQuestion", ignore = true)
    @Mapping(target = "practiceQuestion", source = "practiceQuestion")
    @Mapping(target = "reason", source = "request.reason")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "contentSnapshot", source = "contentSnapshot")
    @Mapping(target = "status", constant = "PENDENTE")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    QuestionReportEntity toPracticeEntity(QuestionReportRequestDTO request, UserEntity student,
                                          DirectedPracticeQuestionEntity practiceQuestion, String contentSnapshot);

    QuestionReportResponseDTO toResponseDTO(QuestionReportEntity entity);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    QuestionReportSummaryResponseDTO toSummaryDTO(QuestionReportEntity entity);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "simulationId", source = "simulationQuestion.simulation.id")
    @Mapping(target = "simulationQuestionId", source = "simulationQuestion.id")
    @Mapping(target = "practiceId", source = "practiceQuestion.practice.id")
    @Mapping(target = "practiceQuestionId", source = "practiceQuestion.id")
    QuestionReportDetailResponseDTO toDetailDTO(QuestionReportEntity entity);
}