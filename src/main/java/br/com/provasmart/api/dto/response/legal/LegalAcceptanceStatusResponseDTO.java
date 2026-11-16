package br.com.provasmart.api.dto.response.legal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pendências de aceite dos documentos legais do usuário autenticado")
public record LegalAcceptanceStatusResponseDTO(
        boolean termsAcceptanceRequired,
        boolean privacyAcceptanceRequired
) {
}