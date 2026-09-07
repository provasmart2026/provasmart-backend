package br.com.provasmart.api.dto.request.question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "DTO para representar uma questão")
public record QuestionRequestDTO(

        @NotBlank
        @Schema(description = "O enunciado da questão", example = "Qual é a capital da França?")
        String statement,

        @NotBlank
        @Schema(description = "A explicação da questão", example = "A capital da França é Paris, que é conhecida por " +
                "sua história, cultura e arquitetura.")
        String explanation,

        @NotNull
        @Schema(description = "O ID do assunto à qual a questão pertence", example = "123e4567-e89b-12d3-a456-" +
                "426614174000")
        UUID subjectId,

        @Valid
        @NotNull
        @Size(min = 5, max = 5, message = "A lista de alternativas deve conter exatamente 5 elementos")
        @Schema(description = "A lista de alternativas da questão")
        List< AlternativeRequestDTO> alternatives

) {
}
