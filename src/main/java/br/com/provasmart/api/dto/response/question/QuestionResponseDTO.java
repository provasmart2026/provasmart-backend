package br.com.provasmart.api.dto.response.question;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "DTO para representar uma questão")
public record QuestionResponseDTO(

        @Schema(description = "O ID da questão", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "O enunciado da questão", example = "Qual é a capital da França?")
        String statement,

        @Schema(description = "A explicação da questão", example = "A capital da França é Paris, que é conhecida por " +
                "sua história, cultura e arquitetura.")
        String explanation,

        @Schema(description = "O ID do assunto à qual a questão pertence", example = "123e4567-e89b-12d3-a456-" +
                "426614174000")
        UUID subjectId,

        @Schema(description = "O nome da assunto à qual a questão pertence", example = "História francesa")
        String subjectName,

        @Schema(description = "Indica se a questão está ativa", example = "true")
        boolean active,

        @Schema(description = "As alternativas da questão")
        List<AlternativeResponseDTO> alternatives,

        @Schema(description = "A data e hora de criação da questão", example = "2023-01-01T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "A data e hora da última atualização da questão", example = "2023-01-01T12:00:00")
        LocalDateTime updatedAt
) {
}
