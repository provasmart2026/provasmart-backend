package br.com.provasmart.api.service;

import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import br.com.provasmart.api.dto.request.legal.LegalAcceptanceRequestDTO;
import br.com.provasmart.api.dto.response.legal.LegalDocumentResponseDTO;
import br.com.provasmart.api.dto.response.legal.LegalAcceptanceStatusResponseDTO;

public interface ILegalDocumentService {
    LegalDocumentResponseDTO findCurrent(LegalDocumentTypeEnum type);

    LegalAcceptanceStatusResponseDTO findAcceptanceStatus();

    void acceptCurrentDocuments(LegalAcceptanceRequestDTO request);
}
