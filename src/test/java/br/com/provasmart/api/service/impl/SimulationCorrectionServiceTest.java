package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationAnswerEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.simulation.SimulationQuestionCorrectionResponseDTO;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.simulation.ISimulationAlternativeMapper;
import br.com.provasmart.api.mapper.simulation.ISimulationQuestionCorrectionMapper;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationCorrectionServiceTest {

    @Mock private ISimulationRepository simulationRepository;
    @Mock private ICurrentActorService currentActorService;
    @Spy private ISimulationQuestionCorrectionMapper correctionMapper =
            Mappers.getMapper(ISimulationQuestionCorrectionMapper.class);
    @InjectMocks private SimulationService simulationService;

    private final UUID simulationId = UUID.randomUUID();
    private final UUID studentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(correctionMapper, "iSimulationAlternativeMapper",
                Mappers.getMapper(ISimulationAlternativeMapper.class));
        when(currentActorService.getCurrentUserId()).thenReturn(studentId);
    }

    @Test
    void returnsQuestionsInOrderWithTheirRecordedCorrection() {
        var correct = question(1, true);
        var incorrect = question(2, false);
        var legacy = question(3, null);
        allowLookup(SimulationStatusEnum.FINALIZADO, List.of(legacy, incorrect, correct));

        var result = simulationService.findCorrection(simulationId);

        assertEquals(List.of(1, 2, 3),
                result.stream().map(SimulationQuestionCorrectionResponseDTO::position).toList());
        assertEquals(Boolean.TRUE, result.get(0).isCorrect());
        assertEquals(Boolean.FALSE, result.get(1).isCorrect());
        assertEquals(incorrect.getAnswer().getSelectedAlternative().getId(), result.get(1).selectedAlternativeId());
        assertEquals(incorrect.getCorrectAlternativeId(), result.get(1).correctAlternativeId());
        assertEquals(incorrect.getQuestion().getExplanation(), result.get(1).explanation());
        assertEquals(2, result.get(1).alternatives().size());
        assertNull(result.get(2).correctAlternativeId());
        assertNull(result.get(2).isCorrect());
    }

    @ParameterizedTest
    @EnumSource(value = SimulationStatusEnum.class, names = {"EM_ANDAMENTO", "CANCELADO"})
    void rejectsCorrectionBeforeFinalization(SimulationStatusEnum status) {
        allowLookup(status, List.of(question(1, true)));

        assertThrows(ConflictException.class, () -> simulationService.findCorrection(simulationId));
        verifyNoInteractions(correctionMapper);
    }

    @Test
    void rejectsSimulationNotOwnedByAuthenticatedStudent() {
        when(simulationRepository.findByIdAndStudent_Id(simulationId, studentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> simulationService.findCorrection(simulationId));
        verify(simulationRepository).findByIdAndStudent_Id(simulationId, studentId);
        verifyNoInteractions(correctionMapper);
    }

    private void allowLookup(SimulationStatusEnum status, List<SimulationQuestionEntity> questions) {
        var simulation = new SimulationEntity();
        simulation.setId(simulationId);
        simulation.setStatus(status);
        simulation.setQuestions(questions);
        when(simulationRepository.findByIdAndStudent_Id(simulationId, studentId)).thenReturn(Optional.of(simulation));
    }

    private static SimulationQuestionEntity question(int position, Boolean correct) {
        var recordedAlternative = new AlternativeEntity();
        recordedAlternative.setId(UUID.randomUUID());
        var otherAlternative = new AlternativeEntity();
        otherAlternative.setId(UUID.randomUUID());
        otherAlternative.setCorrect(true);

        var question = new QuestionEntity();
        question.setExplanation("Explicação da questão " + position);
        question.setAlternatives(List.of(recordedAlternative, otherAlternative));

        var answer = new SimulationAnswerEntity();
        answer.setSelectedAlternative(Boolean.TRUE.equals(correct) ? recordedAlternative : otherAlternative);
        answer.setIsCorrect(correct);

        var simulationQuestion = new SimulationQuestionEntity();
        simulationQuestion.setId(UUID.randomUUID());
        simulationQuestion.setPosition(position);
        simulationQuestion.setQuestion(question);
        simulationQuestion.setAnswer(answer);
        simulationQuestion.setCorrectAlternativeId(correct == null ? null : recordedAlternative.getId());
        return simulationQuestion;
    }
}
