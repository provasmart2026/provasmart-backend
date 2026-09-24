package br.com.provasmart.api.dto.response.user;

import br.com.provasmart.api.domain.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "DTO para resposta de usuário")
public record UserResponseDTO(

        @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Nome do usuário", example = "João da Silva")
        String name,

        @Schema(description = "Email do usuário", example = "joao.silva@example.com")
        String email,

        @Schema(description = "Perfil de acesso do usuário", example = "ESTUDANTE")
        RoleEnum role,

        @Schema(description = "Indica se o usuário está ativo", example = "true")
        boolean active,

        @Schema(description = "Indica se o usuário solicitou a exclusão", example = "true")
        boolean deletedRequested,

        @Schema(description = "Data da solicitação de exclusão do usuário", example = "2023-01-01T00:00:00")
        LocalDateTime deletedRequestedAt,

        @Schema(description = "Versão dos Termos de Uso aceita pelo usuário")
        String termsVersion,

        @Schema(description = "Data do aceite dos Termos de Uso")
        LocalDateTime termsAcceptedAt,

        @Schema(description = "Versão da Política de Privacidade aceita pelo usuário")
        String privacyVersion,

        @Schema(description = "Data do aceite da Política de Privacidade")
        LocalDateTime privacyAcceptedAt,

        @Schema(description = "Data de criação do usuário", example = "2023-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Data de atualização do usuário", example = "2023-01-01T00:00:00")
        LocalDateTime updatedAt
) {
}
