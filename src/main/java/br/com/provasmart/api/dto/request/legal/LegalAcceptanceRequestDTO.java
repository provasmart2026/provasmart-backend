package br.com.provasmart.api.dto.request.legal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Aceite explícito das versões dos documentos legais apresentadas ao usuário")
public record LegalAcceptanceRequestDTO(
        @NotNull @AssertTrue(message = "É necessário aceitar os Termos de Uso")
        Boolean acceptTerms,
        @NotNull @AssertTrue(message = "É necessário aceitar a Política de Privacidade")
        Boolean acceptPrivacyPolicy,
        @NotBlank @Size(max = 20)
        String termsVersion,
        @NotBlank @Size(max = 20)
        String privacyVersion
) {
}