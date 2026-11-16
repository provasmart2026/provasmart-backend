package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.RoleEnum;
import br.com.provasmart.api.dto.request.user.UpdateUserNameRequestDTO;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.user.IUserMapper;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserNameServiceTest {
    @Mock private IUserRepository userRepository;
    @Mock private ICurrentActorService currentActorService;
    @Spy private IUserMapper userMapper = Mappers.getMapper(IUserMapper.class);
    @InjectMocks private UserService service;

    @ParameterizedTest
    @EnumSource(RoleEnum.class)
    void updatesOnlyAuthenticatedUsersNameAndUpdateDate(RoleEnum role) {
        var id = UUID.randomUUID();
        var user = new UserEntity();
        user.setId(id);
        user.setName("Nome antigo");
        user.setEmail("teste@example.com");
        user.setPassword("hash-preservado");
        user.setRole(role);
        user.setActive(true);
        user.setTermsVersion("1.2");
        user.setPrivacyVersion("1.1");
        var createdAt = LocalDateTime.of(2026, 1, 1, 12, 0);
        user.setCreatedAt(createdAt);
        when(currentActorService.getCurrentUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        var before = LocalDateTime.now();

        var result = service.updateCurrentUserName(new UpdateUserNameRequestDTO("  Nome atualizado  "));

        assertEquals(id, result.id());
        assertEquals("Nome atualizado", result.name());
        assertFalse(result.updatedAt().isBefore(before));
        assertEquals("teste@example.com", result.email());
        assertEquals("hash-preservado", user.getPassword());
        assertEquals(role, result.role());
        assertTrue(result.active());
        assertEquals("1.2", result.termsVersion());
        assertEquals("1.1", result.privacyVersion());
        assertEquals(createdAt, result.createdAt());
        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }

    @Test
    void rejectsMissingAuthenticatedUser() {
        var id = UUID.randomUUID();
        when(currentActorService.getCurrentUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateCurrentUserName(new UpdateUserNameRequestDTO("Nome")));
        verify(userRepository, never()).save(any());
    }
}