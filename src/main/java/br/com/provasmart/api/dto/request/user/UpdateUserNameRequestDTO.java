package br.com.provasmart.api.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserNameRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String name
) {
}