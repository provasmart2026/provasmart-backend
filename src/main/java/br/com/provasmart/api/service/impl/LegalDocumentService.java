package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import br.com.provasmart.api.dto.request.legal.LegalAcceptanceRequestDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.dto.response.legal.LegalDocumentResponseDTO;
import br.com.provasmart.api.dto.response.legal.LegalAcceptanceStatusResponseDTO;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
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
    private final IUserRepository userRepository;
    private final ICurrentActorService currentActorService;

    @Override
    @Transactional
    public void acceptCurrentDocuments(LegalAcceptanceRequestDTO request) {
        if (!Boolean.TRUE.equals(request.acceptTerms())
                || !Boolean.TRUE.equals(request.acceptPrivacyPolicy())) {
            throw new BadRequestException("É necessário confirmar o aceite dos documentos legais.");
        }
        var user = userRepository.findById(currentActorService.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        var terms = findCurrent(LegalDocumentTypeEnum.TERMOS_DE_USO);
        var privacy = findCurrent(LegalDocumentTypeEnum.POLITICA_DE_PRIVACIDADE);
        if (!terms.version().equals(request.termsVersion())
                || !privacy.version().equals(request.privacyVersion())) {
            throw new ConflictException("Os documentos legais foram atualizados. Consulte as versões vigentes antes de aceitar.");
        }

        var now = LocalDateTime.now();
        boolean changed = false;
        if (!terms.version().equals(user.getTermsVersion())) {
            user.setTermsVersion(terms.version());
            user.setTermsAcceptedAt(now);
            changed = true;
        }
        if (!privacy.version().equals(user.getPrivacyVersion())) {
            user.setPrivacyVersion(privacy.version());
            user.setPrivacyAcceptedAt(now);
            changed = true;
        }
        if (changed) {
            user.setUpdatedAt(now);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LegalAcceptanceStatusResponseDTO findAcceptanceStatus() {
        var user = userRepository.findById(currentActorService.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        var terms = findCurrent(LegalDocumentTypeEnum.TERMOS_DE_USO);
        var privacy = findCurrent(LegalDocumentTypeEnum.POLITICA_DE_PRIVACIDADE);

        return new LegalAcceptanceStatusResponseDTO(
                !terms.version().equals(user.getTermsVersion()),
                !privacy.version().equals(user.getPrivacyVersion()));
    }

    @Override
    @Transactional(readOnly = true)
    public LegalDocumentResponseDTO findCurrent(LegalDocumentTypeEnum type) {
        var document = legalDocumentRepository
                .findFirstByTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDescCreatedAtDescIdDesc(type, LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("Documento legal vigente não encontrado."));
        return legalDocumentMapper.toResponseDTO(document);
    }
}
