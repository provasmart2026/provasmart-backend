package br.com.provasmart.api.dto.response.simulation;

import br.com.provasmart.api.domain.enums.SimulationStatusEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SimulationResponseDTO(

        UUID id,

        UUID studentId,

        SimulationStatusEnum status,

        LocalDateTime startedAt,

        LocalDateTime finishedAt,

        List<SimulationQuestionResponseDTO> questions
) {
}
