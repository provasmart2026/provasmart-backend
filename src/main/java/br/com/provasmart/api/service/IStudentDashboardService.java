package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.response.dashboard.StudentDashboardResponseDTO;

public interface IStudentDashboardService {

    StudentDashboardResponseDTO getSimulationDashboard();

    StudentDashboardResponseDTO getDirectedPracticeDashboard();
}
