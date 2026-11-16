package br.com.provasmart.api.controller;

import br.com.provasmart.api.domain.enums.StudyPlanStatusEnum;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanActivityCompletionRequestDTO;
import br.com.provasmart.api.dto.request.studyplan.StudyPlanCreateRequestDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanResponseDTO;
import br.com.provasmart.api.dto.response.studyplan.StudyPlanSummaryResponseDTO;
import br.com.provasmart.api.service.IStudyPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/study-plans")
@RequiredArgsConstructor
public class StudyPlanController {

    private final IStudyPlanService studyPlanService;

    @PostMapping
    public ResponseEntity<StudyPlanResponseDTO> create(@Valid @RequestBody StudyPlanCreateRequestDTO requestDTO) {
        var plan = studyPlanService.create(requestDTO);
        var location = URI.create("/study-plans/" + plan.id());
        return ResponseEntity.created(location).body(plan);
    }

    @GetMapping("/current")
    public ResponseEntity<StudyPlanResponseDTO> findCurrent() {
        return studyPlanService.findCurrent()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping
    public ResponseEntity<Page<StudyPlanSummaryResponseDTO>> findAll(
            @RequestParam(required = false) StudyPlanStatusEnum status, Pageable pageable) {
        return ResponseEntity.ok(studyPlanService.findAll(status, pageable));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<StudyPlanResponseDTO> findById(@PathVariable UUID planId) {
        var plan = studyPlanService.findById(planId);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{planId}/activities/{activityId}/completion")
    public ResponseEntity<StudyPlanResponseDTO> updateActivityCompletion(@PathVariable UUID planId,@PathVariable UUID activityId,
            @Valid @RequestBody StudyPlanActivityCompletionRequestDTO requestDTO) {
        var plan = studyPlanService.updateActivityCompletion(planId, activityId, requestDTO);
        return ResponseEntity.ok(plan);
    }
}