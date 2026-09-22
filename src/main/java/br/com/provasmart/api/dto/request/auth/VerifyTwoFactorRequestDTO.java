package br.com.provasmart.api.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "DTO para requisição de verificação de autenticação de dois fatores")
public record VerifyTwoFactorRequestDTO(

        @NotBlank
        @Email
        @Schema(description = "Email do usuário")
        String email,

        @NotBlank
        @Pattern(regexp = "\\d{6}", message = "O código de verificação deve conter exatamente 6 dígitos")
        @Schema(description = "Código de verificação de dois fatores", example = "123456")
        String code
) {
}
