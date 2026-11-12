package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.RoleEnum;
import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import br.com.provasmart.api.dto.request.user.UserRequestDTO;
import br.com.provasmart.api.dto.response.user.UserResponseDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.user.IUserMapper;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.repository.studyplans.IStudyPlanRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.IUserService;
import br.com.provasmart.api.service.ILegalDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    private final IUserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final ICurrentActorService currentActorService;
    private final IStudyPlanRepository studyPlanRepository;
    private final ILegalDocumentService legalDocumentService;

    @Override
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        log.info("Creating a new user.");

        var normalizedEmail = normalizeEmail(userRequestDTO);

        validateEmailExists(normalizedEmail);

        validateLegalDocumentVersions(userRequestDTO);

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
    public UserResponseDTO findCurrentUser() {
        log.info("Finding current user");

        var userId = currentActorService.getCurrentUserId();
        var userEntity = findUserById(userId);

        return mapToDTO(userEntity);
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

        validateUserIsNotAdmin(userEntity);

        userEntity.setActive(false);
        userEntity.setUpdatedAt(LocalDateTime.now());
        save(userEntity);
    }

    @Override
    public void requestDelete() {
        var userId = currentActorService.getCurrentUserId();
        var userEntity = findUserById(userId);
        log.info("Requesting deletion for user with id: {}", userId);
        markDeletionRequest(userEntity);
        save(userEntity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting user with id: {}", id);
        var userEntity = findUserById(id);

        validateUserIsNotAdmin(userEntity);
        validateDeletionRequested(userEntity);

        studyPlanRepository.deleteByStudent_Id(id);
        studyPlanRepository.flush();

        userRepository.delete(userEntity);
    }

    private void validateLegalDocumentVersions(UserRequestDTO request) {
        var terms = legalDocumentService.findCurrent(LegalDocumentTypeEnum.TERMOS_DE_USO);
        var privacy = legalDocumentService.findCurrent(LegalDocumentTypeEnum.POLITICA_DE_PRIVACIDADE);

        if (!terms.version().equals(request.termsVersion())
                || !privacy.version().equals(request.privacyVersion())) {
            throw new ConflictException("Os documentos legais foram atualizados. Consulte os termos e a política vigentes antes de concluir o cadastro.");
        }
    }

    private void validateUserIsNotAdmin(UserEntity userEntity) {
        if (userEntity.getRole() == RoleEnum.ADMIN) {
            log.error("Operation not allowed for admin user with id: {}", userEntity.getId());
            throw new BadRequestException("Esta operação não é permitida para usuário administrador.");
        }
    }

    private void validateDeletionRequested(UserEntity userEntity) {
        if (!userEntity.isDeletionRequested()) {
            log.error("Attempted to delete user with id: {} without a deletion request.", userEntity.getId());
            throw new ConflictException("O usuário não solicitou a exclusão.");
        }
    }

    private static void markDeletionRequest(UserEntity userEntity) {
        userEntity.setDeletionRequested(true);
        userEntity.setDeletionRequestedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
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
        log.info("Saving user.");
        return userRepository.save(userEntity);
    }

    private UserEntity mapToEntity(UserRequestDTO userRequestDTO) {
        log.info("Mapping UserRequestDTO to UserEntity.");
        return userMapper.toEntity(userRequestDTO);
    }

    private void validateEmailExists(String normalizedEmail) {
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.error("User creation rejected: email already registered.");
            throw new ConflictException("Usuário com este e-mail já existe.");
        }
    }

    private UserEntity findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User with id {} not found.", id);
            return new NotFoundException("Usuário não encontrado.");
        });
    }
}
