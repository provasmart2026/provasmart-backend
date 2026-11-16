package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.legal.LegalDocumentEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import br.com.provasmart.api.dto.response.legal.LegalDocumentResponseDTO;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.dto.request.legal.LegalAcceptanceRequestDTO;
import br.com.provasmart.api.mapper.legal.ILegalDocumentMapper;
import br.com.provasmart.api.repository.legal.ILegalDocumentRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LegalDocumentServiceTest {
    @Mock private ILegalDocumentRepository legalDocumentRepository;
    @Mock private ILegalDocumentMapper legalDocumentMapper;
    @Mock private IUserRepository userRepository;
    @Mock private ICurrentActorService currentActorService;
    @InjectMocks private LegalDocumentService service;

    @ParameterizedTest
    @CsvSource({"1.1,1.1,false,false", "1.0,1.1,true,false",
            "1.1,1.0,false,true", "1.0,1.0,true,true"})
    void comparesEachAcceptedVersionIndependently(String termsVersion, String privacyVersion,
                                                 boolean termsRequired, boolean privacyRequired) {
        var user = authenticatedUser();
        user.setTermsVersion(termsVersion);
        user.setPrivacyVersion(privacyVersion);
        currentDocument(LegalDocumentTypeEnum.TERMOS_DE_USO);
        currentDocument(LegalDocumentTypeEnum.POLITICA_DE_PRIVACIDADE);

        var result = service.findAcceptanceStatus();

        assertEquals(termsRequired, result.termsAcceptanceRequired());
        assertEquals(privacyRequired, result.privacyAcceptanceRequired());
        assertEquals(termsVersion, user.getTermsVersion());
        assertEquals(privacyVersion, user.getPrivacyVersion());
        verify(userRepository, never()).save(any());
    }

    @Test
    void rejectsMissingAuthenticatedUser() {
        var id = UUID.randomUUID();
        when(currentActorService.getCurrentUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, service::findAcceptanceStatus);
        verifyNoInteractions(legalDocumentRepository, legalDocumentMapper);
    }

    @Test
    void rejectsMissingCurrentDocumentInsteadOfReportingNoPendingAcceptance() {
        authenticatedUser();
        when(legalDocumentRepository
                .findFirstByTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDescCreatedAtDescIdDesc(
                        eq(LegalDocumentTypeEnum.TERMOS_DE_USO), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, service::findAcceptanceStatus);
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({"1.0,1.0", "1.0,1.1", "1.1,1.0", "1.1,1.1"})
    void recordsOnlyPendingAcceptancesAndPreservesExistingDates(String termsVersion, String privacyVersion) {
        var user = authenticatedUser();
        var previousDate = LocalDateTime.of(2026, 1, 1, 12, 0);
        user.setTermsVersion(termsVersion);
        user.setPrivacyVersion(privacyVersion);
        user.setTermsAcceptedAt(previousDate);
        user.setPrivacyAcceptedAt(previousDate);
        user.setUpdatedAt(previousDate);
        currentDocument(LegalDocumentTypeEnum.TERMOS_DE_USO);
        currentDocument(LegalDocumentTypeEnum.POLITICA_DE_PRIVACIDADE);
        var before = LocalDateTime.now();

        service.acceptCurrentDocuments(new LegalAcceptanceRequestDTO(true, true, "1.1", "1.1"));

        assertEquals("1.1", user.getTermsVersion());
        assertEquals("1.1", user.getPrivacyVersion());
        if (termsVersion.equals("1.1")) assertEquals(previousDate, user.getTermsAcceptedAt());
        else assertFalse(user.getTermsAcceptedAt().isBefore(before));
        if (privacyVersion.equals("1.1")) assertEquals(previousDate, user.getPrivacyAcceptedAt());
        else assertFalse(user.getPrivacyAcceptedAt().isBefore(before));
        boolean changed = !termsVersion.equals("1.1") || !privacyVersion.equals("1.1");
        verify(userRepository, times(changed ? 1 : 0)).save(user);
        if (changed) assertFalse(user.getUpdatedAt().isBefore(before));
        else assertEquals(previousDate, user.getUpdatedAt());
    }

    @ParameterizedTest
    @CsvSource({"1.0,1.1", "1.1,1.0"})
    void rejectsOutdatedPresentedDocumentsWithoutChangingEitherAcceptance(String terms, String privacy) {
        var user = authenticatedUser();
        var previousDate = LocalDateTime.of(2026, 1, 1, 12, 0);
        user.setTermsVersion("1.0");
        user.setPrivacyVersion("1.0");
        user.setTermsAcceptedAt(previousDate);
        user.setPrivacyAcceptedAt(previousDate);
        currentDocument(LegalDocumentTypeEnum.TERMOS_DE_USO);
        currentDocument(LegalDocumentTypeEnum.POLITICA_DE_PRIVACIDADE);

        assertThrows(ConflictException.class, () -> service.acceptCurrentDocuments(
                new LegalAcceptanceRequestDTO(true, true, terms, privacy)));

        assertEquals("1.0", user.getTermsVersion());
        assertEquals("1.0", user.getPrivacyVersion());
        assertEquals(previousDate, user.getTermsAcceptedAt());
        assertEquals(previousDate, user.getPrivacyAcceptedAt());
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource(value = {"false,true", "true,false", "null,true", "true,null"}, nullValues = "null")
    void requiresExplicitAcceptance(Boolean terms, Boolean privacy) {
        assertThrows(BadRequestException.class, () -> service.acceptCurrentDocuments(
                new LegalAcceptanceRequestDTO(terms, privacy, "1.1", "1.1")));
        verifyNoInteractions(userRepository, legalDocumentRepository, currentActorService);
    }

    @Test
    void validatesRequiredVersionsAndAcceptancesAtTheRequestBoundary() {
        try (var factory = jakarta.validation.Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new LegalAcceptanceRequestDTO(true, true, "1.1", "1.1")).isEmpty());
            assertFalse(validator.validate(new LegalAcceptanceRequestDTO(false, true, "1.1", "1.1")).isEmpty());
            assertFalse(validator.validate(new LegalAcceptanceRequestDTO(true, null, "1.1", "1.1")).isEmpty());
            assertFalse(validator.validate(new LegalAcceptanceRequestDTO(true, true, " ", "1.1")).isEmpty());
            assertFalse(validator.validate(new LegalAcceptanceRequestDTO(true, true, "1.1", null)).isEmpty());
            assertFalse(validator.validate(new LegalAcceptanceRequestDTO(true, true, "x".repeat(21), "1.1")).isEmpty());
        }
    }

    private UserEntity authenticatedUser() {
        var id = UUID.randomUUID();
        var user = new UserEntity();
        user.setId(id);
        when(currentActorService.getCurrentUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        return user;
    }

    private void currentDocument(LegalDocumentTypeEnum type) {
        var entity = new LegalDocumentEntity();
        var now = LocalDateTime.now();
        when(legalDocumentRepository
                .findFirstByTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDescCreatedAtDescIdDesc(
                        eq(type), any(LocalDateTime.class)))
                .thenReturn(Optional.of(entity));
        when(legalDocumentMapper.toResponseDTO(entity)).thenReturn(new LegalDocumentResponseDTO(
                UUID.randomUUID(), type, "1.1", "Documento", "{}", now, now));
    }
}