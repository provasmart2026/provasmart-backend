package br.com.provasmart.api.dto.practice;

import br.com.provasmart.api.domain.enums.ExamAreaEnum;

import java.util.List;
import java.util.UUID;

public record DirectedPracticeAnalysisDTO(
        UUID practiceId,
        ExamAreaEnum area,
        int total,
        int correct,
        double percentage,
        List<Breakdown> disciplines,
        List<Breakdown> subjects,
        List<Review> questions
) {
    public record Breakdown(String name, String discipline, int total, int correct, double percentage) {}

    public record Review(
            UUID questionId,
            int position,
            String statement,
            String explanation,
            String discipline,
            String subject,
            UUID selectedAlternativeId,
            UUID correctAlternativeId,
            String selectedLetter,
            String correctLetter,
            boolean correct,
            List<DirectedPracticeDTO.Alternative> alternatives
    ) {}
}
