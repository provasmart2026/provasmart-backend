package br.com.provasmart.api.service;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;

import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import br.com.provasmart.api.dto.response.question.DisciplineResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IDisciplineService {

    List<DisciplineResponseDTO> findAllByExamArea(ExamAreaEnum examArea);

    DisciplineEntity findById(UUID id);
}
