package br.com.provasmart.api.mapper.question;

import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import br.com.provasmart.api.dto.request.question.SubjectRequestDTO;
import br.com.provasmart.api.dto.response.question.SubjectResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ISubjectMapper{

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "subjectRequestDTO.name")
    @Mapping(target = "discipline", source = "discipline")
    SubjectEntity toEntity(DisciplineEntity discipline, SubjectRequestDTO subjectRequestDTO);

    SubjectResponseDTO toResponseDTO(SubjectEntity subjectEntity);
}
