package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.request.question.SubjectRequestDTO;
import br.com.provasmart.api.dto.response.question.SubjectResponseDTO;
import br.com.provasmart.api.service.impl.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/discipline/{disciplineId}")
    public ResponseEntity<List<SubjectResponseDTO>> findAllByDiscipline(@PathVariable UUID disciplineId) {
        var subjects = subjectService.findAllByDiscipline(disciplineId);
        return ResponseEntity.ok(subjects);
    }

    @PostMapping("/discipline/{disciplineId}")
    public ResponseEntity<SubjectResponseDTO> create(@PathVariable UUID disciplineId,
                                                     @RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        var subject = subjectService.create(disciplineId, subjectRequestDTO);
        var location = URI.create("/subjects/" + subject.id());
        return ResponseEntity.created(location).body(subject);
    }
}
