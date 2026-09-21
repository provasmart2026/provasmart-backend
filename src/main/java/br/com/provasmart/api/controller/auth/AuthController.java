package br.com.provasmart.api.controller.auth;

import br.com.provasmart.api.dto.request.auth.ForgotPasswordRequestDTO;
import br.com.provasmart.api.dto.request.auth.LoginRequestDTO;
import br.com.provasmart.api.dto.request.auth.ResetPasswordRequestDTO;
import br.com.provasmart.api.dto.request.auth.VerifyTwoFactorRequestDTO;
import br.com.provasmart.api.dto.response.auth.LoginResponseDTO;
import br.com.provasmart.api.dto.response.auth.TokenResponseDTO;
import br.com.provasmart.api.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        var response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<TokenResponseDTO> verifyTwoFactor(@RequestBody @Valid VerifyTwoFactorRequestDTO verifyTwoFactorRequestDTO) {
        var response = authService.verifyTwoFactor(verifyTwoFactorRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<LoginResponseDTO> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        var response = authService.forgotPassword(forgotPasswordRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<LoginResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO) {
        var response = authService.resetPassword(resetPasswordRequestDTO);
        return ResponseEntity.ok(response);
    }
}
