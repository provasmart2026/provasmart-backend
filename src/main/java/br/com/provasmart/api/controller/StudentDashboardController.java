package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.response.dashboard.StudentDashboardResponseDTO;
import br.com.provasmart.api.service.IStudentDashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student-dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard do estudante")
public class StudentDashboardController {

    private final IStudentDashboardService studentDashboardService;

    @GetMapping("/simulations")
    public ResponseEntity<StudentDashboardResponseDTO> getSimulationDashboard() {
        return ResponseEntity.ok(studentDashboardService.getSimulationDashboard());
    }

    @GetMapping("/directed-practices")
    public ResponseEntity<StudentDashboardResponseDTO> getDirectedPracticeDashboard() {
        return ResponseEntity.ok(studentDashboardService.getDirectedPracticeDashboard());
    }
}
