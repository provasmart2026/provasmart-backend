package br.com.provasmart.api.dto.response.simulation;

import java.util.List;
import java.util.UUID;

public record SimulationQuestionResponseDTO(

        UUID id,

        Integer position,

        UUID questionId,

        String statement,

        List<SimulationAlternativeResponseDTO> alternatives,

        UUID selectedAlternativeId
) {
}
