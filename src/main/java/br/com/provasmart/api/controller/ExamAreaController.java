package br.com.provasmart.api.controller;

import br.com.provasmart.api.domain.entity.questions.ExamAreaEntity;
import br.com.provasmart.api.dto.response.question.ExamAreaResponseDTO;
import br.com.provasmart.api.service.IExamAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exam-areas")
@RequiredArgsConstructor
public class ExamAreaController {

    private final IExamAreaService examAreaService;

    @GetMapping
    public ResponseEntity<List<ExamAreaResponseDTO>> findAll() {
        var examAreas = examAreaService.findAll();
        return ResponseEntity.ok(examAreas);
    }
}
