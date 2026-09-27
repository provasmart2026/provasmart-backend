package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.question.QuestionRequestDTO;
import br.com.provasmart.api.dto.response.question.QuestionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IQuestionService {

    QuestionResponseDTO create(QuestionRequestDTO requestDTO);

    QuestionResponseDTO update(UUID id, QuestionRequestDTO requestDTO);

    void deactivate(UUID id);

    void activate(UUID id);

    QuestionResponseDTO findById(UUID id);

    Page<QuestionResponseDTO> findAll(Pageable pageable);

    Page<QuestionResponseDTO> findAllActive(Pageable pageable);
}
