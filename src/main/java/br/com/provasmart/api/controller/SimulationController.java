package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.request.simulation.SimulationAnswerRequestDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
import br.com.provasmart.api.service.ISimulationService;
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

    @PostMapping
    public ResponseEntity<SimulationResponseDTO> create() {
        var simulation = simulationService.create();
        var location = URI.create("/simulations/" + simulation.id());
        return ResponseEntity.created(location).body(simulation);
    }

    @GetMapping("/{simulationId}")
    public ResponseEntity<SimulationResponseDTO> findById(@PathVariable UUID simulationId) {
        var simulation = simulationService.findById(simulationId);
        return ResponseEntity.ok(simulation);
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
}
