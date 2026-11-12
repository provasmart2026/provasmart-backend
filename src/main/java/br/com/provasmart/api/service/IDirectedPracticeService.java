package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.practice.DirectedPracticeAnalysisDTO;
import br.com.provasmart.api.dto.practice.DirectedPracticeDTO;
import br.com.provasmart.api.dto.response.practice.DirectedPracticeSummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface IDirectedPracticeService {
    Page<DirectedPracticeSummaryResponseDTO> findHistory(Pageable pageable);
    DirectedPracticeDTO create();
    List<DirectedPracticeDTO> list();
    DirectedPracticeDTO findById(UUID id);
    DirectedPracticeDTO answer(UUID id, UUID questionId, UUID alternativeId);
    DirectedPracticeDTO finish(UUID id);
    DirectedPracticeDTO cancel(UUID id);
    DirectedPracticeAnalysisDTO analysis(UUID id);
}
