package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.simulation.SimulationAnswerRequestDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;

import java.util.UUID;

public interface ISimulationService {

    SimulationResponseDTO create(UUID studentId);

    SimulationResponseDTO findById(UUID simulationId);

    SimulationResponseDTO answerQuestion(UUID simulationId, UUID simulationQuestionId, SimulationAnswerRequestDTO answerRequestDTO);

    SimulationResponseDTO finishSimulation(UUID simulationId);
}
