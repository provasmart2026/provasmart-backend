package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.dto.request.user.UserRequestDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.repository.users.IUserRepository;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserEmailConfirmationTest {
    private UserRequestDTO request(String confirmation) {
        return new UserRequestDTO("Nome", "user@example.com", confirmation, "Senha@123", true, true, true, "1.1", "1.1");
    }

    @Test
    void rejectsMismatchBeforeAccessingRepository() {
        var repository = mock(IUserRepository.class);
        var service = new UserService(repository, null, null, null, null, null);
        var error = assertThrows(BadRequestException.class, () -> service.create(request("other@example.com")));
        assertEquals("Os e-mails não coincidem.", error.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void acceptsNormalizedMatchingEmail() {
        var repository = mock(IUserRepository.class);
        when(repository.existsByEmail("user@example.com")).thenThrow(new IllegalStateException("comparison passed"));
        var service = new UserService(repository, null, null, null, null, null);
        assertThrows(IllegalStateException.class, () -> service.create(request(" USER@EXAMPLE.COM ")));
        verify(repository).existsByEmail("user@example.com");
    }

    @Test
    void requiresValidConfirmation() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            for (String confirmation : new String[]{null, "", "invalid"}) {
                assertTrue(validator.validate(request(confirmation)).stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("confirmEmail")));
            }
            assertTrue(validator.validate(request("user@example.com")).isEmpty());
        }
    }
}