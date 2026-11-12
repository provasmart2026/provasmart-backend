package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import br.com.provasmart.api.dto.response.legal.LegalDocumentResponseDTO;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.legal.ILegalDocumentMapper;
import br.com.provasmart.api.repository.legal.ILegalDocumentRepository;
import br.com.provasmart.api.service.ILegalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LegalDocumentService implements ILegalDocumentService {

    private final ILegalDocumentRepository legalDocumentRepository;
    private final ILegalDocumentMapper legalDocumentMapper;

    @Override
    @Transactional(readOnly = true)
    public LegalDocumentResponseDTO findCurrent(LegalDocumentTypeEnum type) {
        var document = legalDocumentRepository
                .findFirstByTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDescCreatedAtDescIdDesc(type, LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("Documento legal vigente não encontrado."));
        return legalDocumentMapper.toResponseDTO(document);
    }
}
