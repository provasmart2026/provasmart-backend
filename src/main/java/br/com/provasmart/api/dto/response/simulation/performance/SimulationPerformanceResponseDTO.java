package br.com.provasmart.api.dto.response.simulation.performance;

import io.swagger.v3.oas.annotations.media.Schema;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import java.util.UUID;
import java.util.List;

@Schema(description = "Análise de desempenho do simulado")
public record SimulationPerformanceResponseDTO(
        @Schema(description = "ID do simulado", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID simulationId,

        @Schema(description = "Resultado geral do simulado")
        SimulationPerformanceSummaryDTO summary,

        @Schema(description = "Todas as áreas participantes empatadas no menor percentual de acertos; " +
                "o rodízio será responsabilidade da Prática Direcionada")
        List<ExamAreaEnum> worstAreas,

        @Schema(description = "Desempenho das áreas que participaram do simulado")
        List<SimulationAreaPerformanceDTO> areas
) {
}