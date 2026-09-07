package br.com.provasmart.api.mapper.question;

import br.com.provasmart.api.domain.entity.questions.ExamAreaEntity;
import br.com.provasmart.api.dto.response.question.ExamAreaResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IExamAreaMapper {

    ExamAreaResponseDTO toResponseDTO(ExamAreaEntity entity);
}
