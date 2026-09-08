package br.com.provasmart.api.mapper.question;

import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import br.com.provasmart.api.dto.response.question.DisciplineResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IDisciplineMapper {

    DisciplineResponseDTO toResponseDTO(DisciplineEntity entity);
}
