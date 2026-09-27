package br.com.provasmart.api.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO para requisição de criação de usuário")
public record UserRequestDTO(

        @NotBlank( message = "o nome do usuário não pode ser vazio")
        @Size(max = 150)
        @Schema(description = "Nome do usuário", example = "João da Silva")
        String name,

        @NotBlank
        @Email(message = "o email do usuário deve ser válido")
        @Size(max = 255)
        @Schema(description = "Email do usuário", example = "joao.silva@example.com")
        String email,

        @NotBlank
        @Size(min = 8, message = "a senha deve ter pelo menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "A senha deve possuir letra maiúscula, letra minúscula, número e caractere especial")
        @Schema(description = "Senha do usuário", example = "Senha123!")
        String password,

        @NotNull
        @AssertTrue(message = "é necessário aceitar os Termos de Uso")
        @Schema(description = "Aceite dos Termos de Uso", example = "true")
        Boolean acceptTerms,

        @NotNull
        @AssertTrue(message = "é necessário aceitar a Política de Privacidade")
        @Schema(description = "Aceite da Política de Privacidade", example = "true")
        Boolean acceptPrivacyPolicy
) {
}
