package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.response.simulation.performance.SimulationPerformanceResponseDTO;

import java.util.UUID;

public interface ISimulationPerformanceService {

    SimulationPerformanceResponseDTO findById(UUID simulationId);
}