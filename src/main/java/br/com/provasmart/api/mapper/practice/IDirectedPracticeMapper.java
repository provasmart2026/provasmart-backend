package br.com.provasmart.api.mapper.practice;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeAlternativeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.dto.practice.DirectedPracticeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IDirectedPracticeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "student")
    @Mapping(target = "sourceSimulation", source = "simulation")
    @Mapping(target = "area", source = "area")
    @Mapping(target = "status", expression = "java(br.com.provasmart.api.domain.enums.DirectedPracticeStatus.EM_ANDAMENTO)")
    @Mapping(target = "startedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "finishedAt", ignore = true)
    @Mapping(target = "questions", ignore = true)
    DirectedPracticeEntity toEntity(UserEntity student, SimulationEntity simulation, ExamAreaEnum area);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "practice", source = "practice")
    @Mapping(target = "originalQuestionId", source = "original.id")
    @Mapping(target = "position", source = "position")
    @Mapping(target = "statement", source = "original.statement")
    @Mapping(target = "explanation", source = "original.explanation")
    @Mapping(target = "disciplineName", source = "original.subject.discipline.name")
    @Mapping(target = "subjectName", source = "original.subject.name")
    @Mapping(target = "selectedAlternativeId", ignore = true)
    @Mapping(target = "answeredAt", ignore = true)
    @Mapping(target = "correctAtFinish", ignore = true)
    @Mapping(target = "alternatives", ignore = true)
    DirectedPracticeQuestionEntity toQuestion(DirectedPracticeEntity practice, QuestionEntity original, int position);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "practiceQuestion", source = "question")
    @Mapping(target = "originalAlternativeId", source = "original.id")
    @Mapping(target = "letter", source = "letter")
    @Mapping(target = "text", source = "original.text")
    @Mapping(target = "correct", source = "original.correct")
    DirectedPracticeAlternativeEntity toAlternative(DirectedPracticeQuestionEntity question, AlternativeEntity original, String letter);

    @Mapping(target = "sourceSimulationId", source = "sourceSimulation.id")
    DirectedPracticeDTO toDTO(DirectedPracticeEntity practice);

    @Mapping(target = "discipline", source = "disciplineName")
    @Mapping(target = "subject", source = "subjectName")
    DirectedPracticeDTO.Question toDTO(DirectedPracticeQuestionEntity question);

    DirectedPracticeDTO.Alternative toDTO(DirectedPracticeAlternativeEntity alternative);
}
