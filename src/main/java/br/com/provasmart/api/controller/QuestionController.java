package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.request.question.QuestionRequestDTO;
import br.com.provasmart.api.dto.response.question.QuestionResponseDTO;
import br.com.provasmart.api.service.impl.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> create(@RequestBody @Valid QuestionRequestDTO requestDTO) {
        var question = questionService.createQuestion(requestDTO);
        var location = URI.create("/questions/" + question.id());
        return ResponseEntity.created(location).body(question);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> update(@PathVariable UUID id,
                                                      @RequestBody @Valid QuestionRequestDTO requestDTO) {
        var question = questionService.updateQuestion(id, requestDTO);
        return ResponseEntity.ok(question);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        questionService.desactiveQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        questionService.actovateQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> findById(@PathVariable UUID id) {
        var question = questionService.findById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping
    public ResponseEntity<Page<QuestionResponseDTO>> findAll(Pageable pageable) {
        var questions = questionService.findAll(pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<QuestionResponseDTO>> findAllActive(Pageable pageable) {
        var questions = questionService.findAllActive(pageable);
        return ResponseEntity.ok(questions);
    }
}
