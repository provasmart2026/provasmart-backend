package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.authentication.AuthenticationCodeEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.AuthenticationCodePurposeEnum;
import br.com.provasmart.api.dto.request.auth.ForgotPasswordRequestDTO;
import br.com.provasmart.api.dto.request.auth.LoginRequestDTO;
import br.com.provasmart.api.dto.request.auth.ResetPasswordRequestDTO;
import br.com.provasmart.api.dto.request.auth.VerifyTwoFactorRequestDTO;
import br.com.provasmart.api.dto.response.auth.LoginResponseDTO;
import br.com.provasmart.api.dto.response.auth.TokenResponseDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.UnauthorizedException;
import br.com.provasmart.api.mapper.authentication.IAuthenticationCodeMapper;
import br.com.provasmart.api.repository.authentication.IAuthenticationCodeRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.IAuthService;
import br.com.provasmart.api.service.IEmailService;
import br.com.provasmart.api.service.IJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IAuthenticationCodeMapper authenticationCodeMapper;
    private final IAuthenticationCodeRepository authenticationCodeRepository;
    private final IEmailService emailService;
    private final IJwtService jwtService;


    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        log.info("Starting login process for email: {}", loginRequestDTO.email());

        var normalizedEmail = normalizeEmail(loginRequestDTO.email());
        var user = findUserByEmail(normalizedEmail);

        validateUserActive(user);
        validatePassword(loginRequestDTO.password(), user);

        invalidatePreviousCodes(user);

        var code = generateTwoFactorCode();
        var encodedCode = passwordEncoder.encode(code);
        var expiresAt = LocalDateTime.now().plusMinutes(5);

        var authenticationCode = mapToEntity(user, encodedCode, expiresAt);

        save(authenticationCode);

        sendEmail(user, code);

        log.info("Two-factor authentication code generated and sent successfully.");

        return new LoginResponseDTO("Código de autenticação enviado para o email do usuário.");

    }

    @Override
    public TokenResponseDTO verifyTwoFactor(VerifyTwoFactorRequestDTO verifyTwoFactorRequestDTO) {
        log.info("Starting two-factor authentication code verification.");

        var normalizedEmail = normalizeEmail(verifyTwoFactorRequestDTO.email());
        var user = findUserByEmail(normalizedEmail);

        validateUserActive(user);

        var authenticationCode = findAuthenticationCode(user);

        validateCodeExpiration(authenticationCode);

        validateAuthenticationCode(verifyTwoFactorRequestDTO.code(), authenticationCode);

        markUsedTrue(authenticationCode);

        var token = jwtService.generateToken(user);

        return new TokenResponseDTO(token);
    }


    @Override
    public LoginResponseDTO forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        log.info("Starting password recovery");

        var normalizedEmail = normalizeEmail(forgotPasswordRequestDTO.email());
        var user = findUserByEmail(normalizedEmail);

        invalidatePreviousPasswordResetCodes(user);

        var code = generateTwoFactorCode();
        var encodedCode = passwordEncoder.encode(code);
        var expiresAt = LocalDateTime.now().plusMinutes(5);

        var authenticationCode = mapToPasswordResetEntity(user, encodedCode, expiresAt);

        save(authenticationCode);
        sendEmailResetPassword(user, code);

        log.info("Password reset code generated and sent successfully.");

        return new LoginResponseDTO("Código de redefinição de senha enviado para o email do usuário.");
    }

    @Override
    public LoginResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        log.info("Starting password reset process.");

        var normalizedEmail = normalizeEmail(resetPasswordRequestDTO.email());
        var user = findUserByEmail(normalizedEmail);

        var authenticationCode = findPasswordResetCode(user);

        validateCodeExpiration(authenticationCode);

        validateAuthenticationCode(resetPasswordRequestDTO.code(), authenticationCode);

        var encodedNewPassword = passwordEncoder.encode(resetPasswordRequestDTO.newPassword());

        setNewPassword(user, encodedNewPassword);

        markUsedTrue(authenticationCode);

        log.info("Password reset successfully");
        return new LoginResponseDTO("Senha redefinida com sucesso.");
    }

    private void setNewPassword(UserEntity user, String encodedNewPassword) {
        log.info("Setting new password");
        user.setPassword(encodedNewPassword);
        log.info("Saving user with new password");
        userRepository.save(user);
    }

    private void sendEmailResetPassword(UserEntity user, String code) {
        log.info("Sending password reset email");
        emailService.sendPasswordResetCode(user.getEmail(), code);
    }

    private void sendEmail(UserEntity user, String code) {
        log.info("Sending email");
        emailService.sendTwoFactorCode(user.getEmail(), code);
    }

    private AuthenticationCodeEntity save(AuthenticationCodeEntity authenticationCode) {
        return authenticationCodeRepository.save(authenticationCode);
    }

    private void markUsedTrue(AuthenticationCodeEntity authenticationCode) {
        authenticationCode.setUsed(true);
        log.info("Marking two-factor authentication code as used.");
        authenticationCodeRepository.save(authenticationCode);
    }

    private AuthenticationCodeEntity mapToEntity(UserEntity user, String encodedCode, LocalDateTime expiresAt) {
        log.info("Mapping user to authentication code entity.");
        return authenticationCodeMapper.toEntity(user, encodedCode, AuthenticationCodePurposeEnum.LOGIN_2FA, expiresAt);
    }

    private AuthenticationCodeEntity mapToPasswordResetEntity(UserEntity user, String encodedCode, LocalDateTime expiresAt) {
        log.info("Mapping user to password reset authentication code entity.");
        return authenticationCodeMapper.toEntity(user, encodedCode, AuthenticationCodePurposeEnum.PASSWORD_RESET, expiresAt);
    }

    private static String normalizeEmail(String email) {
        log.info("Normalizing email for authentication.");
        return email.trim().toLowerCase();
    }

    private UserEntity findUserByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Login attempt with invalid credentials.");
                    return new UnauthorizedException("Email ou senha inválidos.");
                });
    }

    private void validateUserActive(UserEntity user) {
        log.info("Validating user active for email: {}", user.getEmail());
        if (!user.isActive()) {
            log.error("User with email {} is not active.", user.getEmail());
            throw new UnauthorizedException("Usuário não está ativo.");
        }
    }

    private void validatePassword(String password, UserEntity user) {
        log.info("Validating password for user with email: {}", user.getEmail());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Invalid password for user with email: {}", user.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos.");
        }
    }

    private String generateTwoFactorCode() {
        log.info("Generating two-factor authentication code.");
        var secureRandom = new SecureRandom();
        var code = secureRandom.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    private void invalidatePreviousCodes(UserEntity user) {
        log.info("Invalidating previous code for user: {}", user.getEmail());
        var authenticationCodes = authenticationCodeRepository.findAllByUserAndPurposeAndUsedFalse(user, AuthenticationCodePurposeEnum.LOGIN_2FA);
        authenticationCodes.forEach(code -> code.setUsed(true));
        authenticationCodeRepository.saveAll(authenticationCodes);
    }

    private void invalidatePreviousPasswordResetCodes(UserEntity user) {
        log.info("Invalidating previous password reset code for user: {}", user.getEmail());

        var authenticationCodes = authenticationCodeRepository.findAllByUserAndPurposeAndUsedFalse(user, AuthenticationCodePurposeEnum.PASSWORD_RESET);
        authenticationCodes.forEach(code -> code.setUsed(true));
        authenticationCodeRepository.saveAll(authenticationCodes);
    }

    private AuthenticationCodeEntity findAuthenticationCode(UserEntity user) {
        log.info("Finding authentication code");

        return authenticationCodeRepository.findFirstByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(user, AuthenticationCodePurposeEnum.LOGIN_2FA)
                .orElseThrow(() -> {
                    log.error("Two-factor authentication code not found.");
                    return new BadRequestException("Código de autenticação não encontrado.");
                });
    }

    private void validateCodeExpiration(AuthenticationCodeEntity authenticationCode) {
        log.info("Validating two-factor authentication code expiration.");

        if (authenticationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Two-factor authentication code has expired.");
            throw new BadRequestException("Código de autenticação expirado.");
        }
    }

    private void validateAuthenticationCode(String code, AuthenticationCodeEntity authenticationCode) {
        log.info("Validating two-factor authentication code.");

        if (!passwordEncoder.matches(code, authenticationCode.getCode())) {
            log.error("Invalid two-factor authentication code.");
            throw new BadRequestException("Código de autenticação inválido.");
        }
    }

    private AuthenticationCodeEntity findPasswordResetCode(UserEntity user) {
        log.info("Finding password reset code");

        return authenticationCodeRepository.findFirstByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(user, AuthenticationCodePurposeEnum.PASSWORD_RESET)
                .orElseThrow(() -> {
                    log.error("Password reset code not found.");
                    return new BadRequestException("Código de redefinição de senha não encontrado.");
                });
    }
}
