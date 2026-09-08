package br.com.provasmart.api.service;

import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import br.com.provasmart.api.dto.request.question.SubjectRequestDTO;
import br.com.provasmart.api.dto.response.question.SubjectResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ISubjectService {

    List<SubjectResponseDTO> findAllByDiscipline(UUID disciplineId);

    SubjectResponseDTO create(UUID disciplineId, SubjectRequestDTO subjectRequestDTO);

    SubjectEntity findById(UUID subjectId);
}
