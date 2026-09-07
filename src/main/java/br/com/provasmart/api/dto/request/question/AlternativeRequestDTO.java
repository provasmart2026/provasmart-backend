package br.com.provasmart.api.dto.request.question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "DTO para representar uma alternativa de questão")
public record AlternativeRequestDTO(

        @NotBlank
        @Pattern(regexp = "^[A-E]$", message = "A letra da alternativa deve ser uma única letra maiúscula de A a E")
        @Schema(description = "A letra da alternativa, que deve ser uma única letra maiúscula de A a E", example = "A")
        String letter,

        @NotBlank
        @Schema(description = "O texto da alternativa", example = "Esta é a alternativa A")
        String text,

        @NotNull
        @Schema(description = "Indica se a alternativa é correta", example = "true")
        boolean correct

) {
}
