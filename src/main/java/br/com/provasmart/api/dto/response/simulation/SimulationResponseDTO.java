package br.com.provasmart.api.dto.response.simulation;

import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "DTO para resposta de simulação")
public record SimulationResponseDTO(

        @Schema(description = "Unique identifier of the simulation", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID do aluno", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID studentId,

        @Schema(description = "Status da simulação", example = "IN_PROGRESS")
        SimulationStatusEnum status,

        @Schema(description = "Data de início da simulação", example = "2023-01-01T00:00:00Z")
        LocalDateTime startedAt,

        @Schema(description = "Data de término da simulação", example = "2023-01-01T01:00:00Z")
        LocalDateTime finishedAt,

        @Schema(description = "Lista de questões da simulação")
        List<SimulationQuestionResponseDTO> questions
) {
}
