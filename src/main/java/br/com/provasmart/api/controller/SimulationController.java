package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.request.simulation.SimulationAnswerRequestDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
import br.com.provasmart.api.service.ISimulationService;
import br.com.provasmart.api.dto.response.error.ErrorResponseDTO;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationPerformanceResponseDTO;
import br.com.provasmart.api.service.ISimulationPerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final ISimulationService simulationService;
    private final ISimulationPerformanceService simulationPerformanceService;

    @PostMapping
    public ResponseEntity<SimulationResponseDTO> create() {
        var simulation = simulationService.create();
        var location = URI.create("/simulations/" + simulation.id());
        return ResponseEntity.created(location).body(simulation);
    }

    @GetMapping("/current")
    public ResponseEntity<SimulationResponseDTO> findCurrent() {
        return simulationService.findCurrent()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{simulationId}")
    public ResponseEntity<SimulationResponseDTO> findById(@PathVariable UUID simulationId) {
        var simulation = simulationService.findById(simulationId);
        return ResponseEntity.ok(simulation);
    }

    @GetMapping("/{simulationId}/performance")
    @Operation(summary = "Consultar análise de desempenho do simulado",
            description = "Retorna o resultado geral e o desempenho por área, disciplina e assunto de um simulado finalizado do estudante autenticado, incluindo todas as áreas empatadas no menor aproveitamento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Análise de desempenho disponível",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SimulationPerformanceResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso permitido apenas ao perfil ESTUDANTE", content = @Content),
            @ApiResponse(responseCode = "404", description = "Simulado não encontrado para o estudante",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Simulado não finalizado ou sem classificação preservada ou correção suficiente para análise",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<SimulationPerformanceResponseDTO> findPerformanceById(@PathVariable UUID simulationId) {
        var performance = simulationPerformanceService.findById(simulationId);
        return ResponseEntity.ok(performance);
    }

    @PutMapping("/{simulationId}/questions/{simulationQuestionId}/answer")
    public ResponseEntity<SimulationResponseDTO> answerQuestion(@PathVariable UUID simulationId,
                                                               @PathVariable UUID simulationQuestionId,
                                                               @Valid @RequestBody SimulationAnswerRequestDTO answerRequestDTO) {
        var simulation = simulationService.answerQuestion(simulationId, simulationQuestionId, answerRequestDTO);
        return ResponseEntity.ok(simulation);
    }

    @PatchMapping("/{simulationId}/finish")
    public ResponseEntity<SimulationResponseDTO> finishSimulation(@PathVariable UUID simulationId) {
        var simulation = simulationService.finishSimulation(simulationId);
        return ResponseEntity.ok(simulation);
    }

    @PatchMapping("/{simulationId}/cancel")
    public ResponseEntity<SimulationResponseDTO> cancelSimulation(@PathVariable UUID simulationId) {
        var simulation = simulationService.cancelSimulation(simulationId);
        return ResponseEntity.ok(simulation);
    }
}
