package br.com.provasmart.api.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para resposta de token de autenticação")
public record TokenResponseDTO(

        @Schema(description = "Token de autenticação JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
