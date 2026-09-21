package br.com.provasmart.api.dto.response.auth;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para resposta de login")
public record LoginResponseDTO(

        @Schema(description = "Mensagem informando o próximo passo da autenticação", example = "Código de verificação enviado para o email do usuário")
        String message
) {
}
