package br.com.provasmart.api.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "DTO para requisição de redefinição de senha")
public record ResetPasswordRequestDTO(

        @NotBlank
        @Email
        @Schema(description = "Email do usuário", example = "joao@example.com")
        String email,

        @NotBlank
        @Pattern(regexp = "\\d{6}", message = "O código de redefinição deve conter exatamente 6 dígitos")
        @Schema(description = "Código de redefinição", example = "ABC123")
        String code,

        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "A senha deve possuir letra maiúscula, letra minúscula, número e caractere especial")
        @Schema(description = "Nova senha", example = "Senha@123")
        String newPassword
) {
}
