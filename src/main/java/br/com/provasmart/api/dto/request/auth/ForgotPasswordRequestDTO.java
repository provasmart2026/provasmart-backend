package br.com.provasmart.api.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO para requisição de redefinição de senha")
public record ForgotPasswordRequestDTO(

        @NotBlank
        @Email
        @Schema(description = "Email do usuário", example = "joao@example.com")
        String email
) {
}
