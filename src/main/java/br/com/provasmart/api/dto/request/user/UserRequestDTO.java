package br.com.provasmart.api.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank
        @Size(min = 8, message = "a senha deve ter pelo menos 8 caracteres")
        @Pattern(
                regexp = "(?s)(?=.*\\p{Lu})(?=.*\\p{Ll})(?=.*\\p{Nd})(?=.*[\\p{P}\\p{S}]).*",
                message = "a senha deve conter maiúscula, minúscula, número e caractere especial"
        )
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password
) {
    @Override
    public String toString() {
        return "UserRequestDTO[protected]";
    }
}
