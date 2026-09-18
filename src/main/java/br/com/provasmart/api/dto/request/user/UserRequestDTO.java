package br.com.provasmart.api.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para requisição de criação de usuário")
public record UserRequestDTO(

        @NotBlank( message = "o nome do usuário não pode ser vazio")
        @Size(max = 150)
        @Schema(description = "Nome do usuário", example = "João da Silva")
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        @Schema(description = "Email do usuário", example = "joao.silva@example.com")
        String email,

        @NotBlank
        @Size(min = 8, message = "a senha deve ter pelo menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "A senha deve possuir letra maiúscula, letra minúscula, número e caractere especial")
        @Schema(description = "Senha do usuário", example = "Senha123!")
        String password
) {
}
