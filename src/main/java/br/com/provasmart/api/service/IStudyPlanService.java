package br.com.provasmart.api.service;

import br.com.provasmart.api.domain.enums.StudyPlanStatusEnum;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanActivityCompletionRequestDTO;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanCreateRequestDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanResponseDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanSummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IStudyPlanService {

    StudyPlanResponseDTO create(StudyPlanCreateRequestDTO requestDTO);

    Optional<StudyPlanResponseDTO> findCurrent();

    Page<StudyPlanSummaryResponseDTO> findAll(StudyPlanStatusEnum status, Pageable pageable);

    StudyPlanResponseDTO findById(UUID studyPlanId);

    StudyPlanResponseDTO updateActivityCompletion(UUID studyPlanId, UUID activityId,
                                                 StudyPlanActivityCompletionRequestDTO requestDTO);
}
