package br.com.provasmart.api.dto.response.simulation;

import java.util.UUID;

public record SimulationAlternativeResponseDTO(

        UUID id,

        String letter,

        String text
) {
}
