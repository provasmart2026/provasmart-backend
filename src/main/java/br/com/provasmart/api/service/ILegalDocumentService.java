package br.com.provasmart.api.service;

import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import br.com.provasmart.api.dto.response.legal.LegalDocumentResponseDTO;

public interface ILegalDocumentService {
    LegalDocumentResponseDTO findCurrent(LegalDocumentTypeEnum type);
}
