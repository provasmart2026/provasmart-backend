package br.com.provasmart.api.mapper.legal;

import br.com.provasmart.api.domain.entity.legal.LegalDocumentEntity;
import br.com.provasmart.api.dto.response.legal.LegalDocumentResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ILegalDocumentMapper {
    LegalDocumentResponseDTO toResponseDTO(LegalDocumentEntity entity);
}
