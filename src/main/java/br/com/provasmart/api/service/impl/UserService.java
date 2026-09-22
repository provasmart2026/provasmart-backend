package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.dto.request.user.UserRequestDTO;
import br.com.provasmart.api.dto.response.user.UserResponseDTO;
import br.com.provasmart.api.mapper.user.IUserMapper;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    private final IUserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        log.info("Creating a new user with email: {}", userRequestDTO.email());

        var normalizedEmail = normalizeEmail(userRequestDTO);

        validateEmailExists(normalizedEmail);

        var userEntity = mapToEntity(userRequestDTO);

        userEntity.setEmail(normalizedEmail);
        userEntity.setPassword(passwordEncoder.encode(userRequestDTO.password()));

        var savedUser = save(userEntity);

        return mapToDTO(savedUser);
    }


    @Override
    public UserResponseDTO findById(UUID id) {
        log.info("Finding user with id: {}", id);

        var userEntity = findUserById(id);

        return mapToDTO(userEntity);
    }

    @Override
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        log.info("Finding all users with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());

        var userEntities = userRepository.findAll(pageable);

        return userEntities.map(this::mapToDTO);
    }

    @Override
    public void activate(UUID id) {
        log.info("Activating user with id: {}", id);
        var userEntity = findUserById(id);
        userEntity.setActive(true);
        userEntity.setUpdatedAt(LocalDateTime.now());
        save(userEntity);
    }

    @Override
    public void deactivate(UUID id) {
        log.info("Deactivating user with id: {}", id);
        var userEntity = findUserById(id);
        userEntity.setActive(false);
        userEntity.setUpdatedAt(LocalDateTime.now());
        save(userEntity);
    }

    private static String normalizeEmail(UserRequestDTO userRequestDTO) {
        log.info("Normalizing email for user creation.");
        return userRequestDTO.email().trim().toLowerCase();
    }

    private UserResponseDTO mapToDTO(UserEntity savedUser) {
        log.info("Mapping UserEntity to UserResponseDTO for user with id: {}", savedUser.getId());
        return userMapper.toResponseDTO(savedUser);
    }

    private UserEntity save(UserEntity userEntity) {
        log.info("Saving user with email: {}", userEntity.getEmail());
        return userRepository.save(userEntity);
    }


    private UserEntity mapToEntity(UserRequestDTO userRequestDTO) {
        log.info("Mapping UserRequestDTO to UserEntity.");
        return userMapper.toEntity(userRequestDTO);
    }

    private void validateEmailExists(String normalizedEmail) {
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.error("User with email {} already exists.", normalizedEmail);
            throw new IllegalArgumentException("Usuário com este e-mail já existe.");
        }
    }

    private UserEntity findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User with id {} not found.", id);
            return new IllegalArgumentException("Usuário não encontrado.");
        });
    }
}
