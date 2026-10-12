package br.com.provasmart.api.mapper.simulation;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.dto.response.simulation.SimulationQuestionResponseDTO;
import br.com.provasmart.api.exception.ConflictException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {ISimulationAlternativeMapper.class})
public interface ISimulationQuestionMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "simulation", source = "simulation")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "position", source = "position")
    @Mapping(target = "answer", ignore = true)
    @Mapping(target = "correctAlternativeId", expression = "java(correctAlternativeId(question))")
    SimulationQuestionEntity toEntity(SimulationEntity simulation, QuestionEntity question, Integer position);

    @Mapping(target = "questionId", source = "entity.question.id")
    @Mapping(target = "statement", source = "entity.question.statement")
    @Mapping(target = "alternatives", source = "entity.question.alternatives")
    @Mapping(target = "selectedAlternativeId", source = "entity.answer.selectedAlternative.id")
    SimulationQuestionResponseDTO toDTO(SimulationQuestionEntity entity);

    default UUID correctAlternativeId(QuestionEntity question) {
        var correctAlternatives = question.getAlternatives().stream()
                .filter(AlternativeEntity::isCorrect)
                .toList();

        if (correctAlternatives.size() != 1 || correctAlternatives.getFirst().getId() == null) {
            throw new ConflictException("A questão deve possuir exatamente uma alternativa correta com identificador válido.");
        }

        return correctAlternatives.getFirst().getId();
    }
}
