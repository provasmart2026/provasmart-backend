package br.com.provasmart.api.repository.authentication;

import br.com.provasmart.api.domain.entity.authentication.AuthenticationCodeEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.AuthenticationCodePurposeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAuthenticationCodeRepository extends JpaRepository<AuthenticationCodeEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AuthenticationCodeEntity> findTopByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(UserEntity user, AuthenticationCodePurposeEnum purpose);

    List<AuthenticationCodeEntity> findAllByUserAndPurposeAndUsedFalse (UserEntity user, AuthenticationCodePurposeEnum purpose);

    Optional<AuthenticationCodeEntity> findFirstByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(UserEntity user, AuthenticationCodePurposeEnum purpose);
}
