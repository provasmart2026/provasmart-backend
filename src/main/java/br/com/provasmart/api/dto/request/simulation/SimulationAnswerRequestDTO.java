package br.com.provasmart.api.dto.request.simulation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO para envio de resposta de simulação")
public record SimulationAnswerRequestDTO(

        @NotNull(message = "A alternativa selecionada é obrigatória")
        UUID alternativeId
) {
}
