package br.com.provasmart.api.controller;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam-areas")
public class ExamAreaController {

    @GetMapping
    public ResponseEntity<ExamAreaEnum[]> findAll() {
        return ResponseEntity.ok(ExamAreaEnum.values());
    }
}
