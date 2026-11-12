package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.simulation.SimulationAnswerRequestDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationQuestionCorrectionResponseDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationSummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISimulationService {

    Page<SimulationSummaryResponseDTO> findHistory(Pageable pageable);

    SimulationResponseDTO create();

    Optional<SimulationResponseDTO> findCurrent();

    SimulationResponseDTO findLatestFinished();

    SimulationResponseDTO findById(UUID simulationId);

    List<SimulationQuestionCorrectionResponseDTO> findCorrection(UUID simulationId);

    SimulationResponseDTO answerQuestion(UUID simulationId, UUID simulationQuestionId, SimulationAnswerRequestDTO answerRequestDTO);

    SimulationResponseDTO finishSimulation(UUID simulationId);

    SimulationResponseDTO cancelSimulation(UUID simulationId);
}
