package br.com.provasmart.api.mapper.question;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.dto.request.question.AlternativeRequestDTO;
import br.com.provasmart.api.dto.response.question.AlternativeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IAlternativeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    AlternativeEntity toEntity(AlternativeRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "letter", ignore = true)
    void toEntityUpdate(AlternativeRequestDTO requestDTO, @MappingTarget AlternativeEntity alternativeEntity);

    AlternativeResponseDTO toResponseDTO(AlternativeEntity entity);
}
