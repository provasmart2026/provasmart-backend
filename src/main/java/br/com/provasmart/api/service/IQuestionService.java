package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.question.QuestionRequestDTO;
import br.com.provasmart.api.dto.response.question.QuestionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IQuestionService {

    QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO);

    QuestionResponseDTO updateQuestion(UUID id, QuestionRequestDTO requestDTO);

    void desactiveQuestion(UUID id);

    void actovateQuestion(UUID id);

    QuestionResponseDTO findById(UUID id);

    Page<QuestionResponseDTO> findAll(Pageable pageable);

    Page<QuestionResponseDTO> findAllActive(Pageable pageable);
}
