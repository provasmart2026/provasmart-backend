package br.com.provasmart.api.dto.practice;

import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DirectedPracticeDTO(
        UUID id,
        UUID sourceSimulationId,
        ExamAreaEnum area,
        DirectedPracticeStatus status,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        List<Question> questions
) {
    public record Question(
            UUID id,
            int position,
            String statement,
            String discipline,
            String subject,
            List<Alternative> alternatives,
            UUID selectedAlternativeId
    ) {}

    public record Alternative(UUID id, String letter, String text) {}
}
