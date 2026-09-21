package br.com.provasmart.api.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO para requisição de login")
public record LoginRequestDTO(

        @NotBlank
        @Email(message = "O email deve ser válido")
        @Schema(description = "Email do usuário", example = "joao@example.com")
        String email,

        @NotBlank
        @Schema(description = "Senha do usuário", example = "Senha@123")
        String password

) {
}
