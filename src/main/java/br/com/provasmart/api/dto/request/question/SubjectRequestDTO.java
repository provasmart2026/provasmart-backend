package br.com.provasmart.api.dto.request.question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para cadastro de um assunto")
public record SubjectRequestDTO(

        @NotBlank
        @Size(max = 150)
        @Schema(description = "Nome do assunto", example = "Equações de 2º grau")
        String name
) {

}
