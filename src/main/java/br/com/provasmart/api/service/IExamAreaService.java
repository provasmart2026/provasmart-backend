package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.response.question.ExamAreaResponseDTO;
import br.com.provasmart.api.dto.response.question.QuestionResponseDTO;

import java.util.List;

public interface IExamAreaService {

    List<ExamAreaResponseDTO> findAll();
}
