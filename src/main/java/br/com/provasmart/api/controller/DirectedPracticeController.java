package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.practice.DirectedPracticeAnalysisDTO;
import br.com.provasmart.api.dto.practice.DirectedPracticeDTO;
import br.com.provasmart.api.service.impl.DirectedPracticeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/directed-practices")
@RequiredArgsConstructor
public class DirectedPracticeController {
    private final DirectedPracticeService service;

    public record AnswerRequest(@NotNull UUID alternativeId) {}

    @PostMapping
    public ResponseEntity<DirectedPracticeDTO> create() {
        var practice = service.create();
        return ResponseEntity.created(URI.create("/directed-practices/" + practice.id())).body(practice);
    }

    @GetMapping
    public ResponseEntity<List<DirectedPracticeDTO>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectedPracticeDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}/questions/{questionId}/answer")
    public ResponseEntity<DirectedPracticeDTO> answer(@PathVariable UUID id, @PathVariable UUID questionId,
            @Valid @RequestBody AnswerRequest request) {
        return ResponseEntity.ok(service.answer(id, questionId, request.alternativeId()));
    }

    @PatchMapping("/{id}/finish")
    public ResponseEntity<DirectedPracticeDTO> finish(@PathVariable UUID id) {
        return ResponseEntity.ok(service.finish(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<DirectedPracticeDTO> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<DirectedPracticeAnalysisDTO> analysis(@PathVariable UUID id) {
        return ResponseEntity.ok(service.analysis(id));
    }
}
