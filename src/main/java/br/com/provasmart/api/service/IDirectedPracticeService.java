package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.practice.DirectedPracticeAnalysisDTO;
import br.com.provasmart.api.dto.practice.DirectedPracticeDTO;
import java.util.List;
import java.util.UUID;

public interface IDirectedPracticeService {
    DirectedPracticeDTO create();
    List<DirectedPracticeDTO> list();
    DirectedPracticeDTO findById(UUID id);
    DirectedPracticeDTO answer(UUID id, UUID questionId, UUID alternativeId);
    DirectedPracticeDTO finish(UUID id);
    DirectedPracticeDTO cancel(UUID id);
    DirectedPracticeAnalysisDTO analysis(UUID id);
}
