package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.response.question.DisciplineResponseDTO;
import br.com.provasmart.api.service.impl.DisciplineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/disciplines")
public class DisciplineController {

    private  final DisciplineService disciplineService;

    @GetMapping("/exam-area/{examAreaId}")
    public ResponseEntity<List<DisciplineResponseDTO>> findAllByExamArea(@PathVariable UUID examAreaId) {
        var disciplines = disciplineService.findAllByExamArea(examAreaId);
        return ResponseEntity.ok(disciplines);
    }
}
