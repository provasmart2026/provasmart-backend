package br.com.provasmart.api.dto.response.studyplan;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.StudyPlanActivityTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Atividade de uma hora do plano de estudos")
public record StudyPlanActivityResponseDTO(
        @Schema(description = "ID da atividade")
        UUID id,

        @Schema(description = "Dia do plano, de 1 a 7")
        Integer dayNumber,

        @Schema(description = "Posição da atividade no dia")
        Integer position,

        @Schema(description = "Área do conhecimento preservada")
        ExamAreaEnum examArea,

        @Schema(description = "ID da disciplina preservado")
        UUID disciplineId,

        @Schema(description = "Nome da disciplina preservado")
        String disciplineName,

        @Schema(description = "ID do assunto preservado")
        UUID subjectId,

        @Schema(description = "Nome do assunto preservado")
        String subjectName,

        @Schema(description = "Tipo da atividade: estudo ou revisão")
        StudyPlanActivityTypeEnum type,

        @Schema(description = "Indica se a atividade foi concluída")
        Boolean completed,

        @Schema(description = "Data e hora de conclusão; ausente quando não concluída")
        LocalDateTime completedAt
) {
}