package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeAlternativeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.response.simulation.performance.SimulationPerformanceResponseDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.repository.practice.IDirectedPracticeRepository;
import br.com.provasmart.api.repository.questions.IQuestionRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.ISimulationPerformanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectedPracticeServiceTest {
    @Mock IDirectedPracticeRepository practices;
    @Mock ISimulationRepository simulations;
    @Mock IQuestionRepository questions;
    @Mock IUserRepository users;
    @Mock ICurrentActorService actor;
    @Mock ISimulationPerformanceService performance;
    @InjectMocks DirectedPracticeService service;

    private final UUID studentId = UUID.randomUUID();

    @Test
    void requiresFinishedGeneralSimulation() {
        when(actor.getCurrentUserId()).thenReturn(studentId);
        when(users.findByIdForUpdate(studentId)).thenReturn(Optional.of(new UserEntity()));
        assertThrows(ConflictException.class, service::create);
        verify(simulations).findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(studentId, SimulationStatusEnum.FINALIZADO);
        verify(practices, never()).saveAndFlush(any());
    }

    @Test
    void refusesSecondPracticeInProgress() {
        when(actor.getCurrentUserId()).thenReturn(studentId);
        when(users.findByIdForUpdate(studentId)).thenReturn(Optional.of(new UserEntity()));
        when(practices.existsByStudent_IdAndStatus(studentId, DirectedPracticeStatus.EM_ANDAMENTO)).thenReturn(true);
        assertThrows(ConflictException.class, service::create);
        verifyNoInteractions(simulations);
    }

    @Test
    void createsTwentySnapshotQuestionsFromWeakestAreaWithoutRevealingAnswers() {
        var student = new UserEntity();
        student.setId(studentId);
        var simulation = new SimulationEntity();
        simulation.setId(UUID.randomUUID());
        List<QuestionEntity> pool = new ArrayList<>();
        for (int i = 0; i < 20; i++) pool.add(question(ExamAreaEnum.MATEMATICA));
        when(actor.getCurrentUserId()).thenReturn(studentId);
        when(users.findByIdForUpdate(studentId)).thenReturn(Optional.of(student));
        when(simulations.findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(studentId, SimulationStatusEnum.FINALIZADO))
                .thenReturn(Optional.of(simulation));
        when(performance.findById(simulation.getId())).thenReturn(new SimulationPerformanceResponseDTO(
                simulation.getId(), null, List.of(ExamAreaEnum.MATEMATICA), List.of()));
        when(questions.findAllByActiveTrueAndSubjectDisciplineExamArea(ExamAreaEnum.MATEMATICA)).thenReturn(pool);
        when(practices.saveAndFlush(any())).thenAnswer(invocation -> {
            DirectedPracticeEntity saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.getQuestions().forEach(item -> {
                item.setId(UUID.randomUUID());
                item.getAlternatives().forEach(alternative -> alternative.setId(UUID.randomUUID()));
            });
            return saved;
        });

        var result = service.create();

        assertEquals(ExamAreaEnum.MATEMATICA, result.area());
        assertEquals(20, result.questions().size());
        assertEquals(20, result.questions().stream().map(item -> item.statement()).distinct().count());
        assertTrue(result.questions().stream().allMatch(item -> item.selectedAlternativeId() == null));
        verify(questions).findAllByActiveTrueAndSubjectDisciplineExamArea(ExamAreaEnum.MATEMATICA);
    }

    @Test
    void doesNotRevealAnalysisBeforeFinish() {
        var practice = ownedPractice(DirectedPracticeStatus.EM_ANDAMENTO);
        assertThrows(ConflictException.class, () -> service.analysis(practice.getId()));
    }

    @Test
    void analysisUsesSavedCorrectionAndSubjectSnapshots() {
        var practice = ownedPractice(DirectedPracticeStatus.FINALIZADA);
        practice.setArea(ExamAreaEnum.MATEMATICA);
        practice.getQuestions().add(reviewQuestion(1, true));
        practice.getQuestions().add(reviewQuestion(2, false));

        var result = service.analysis(practice.getId());

        assertEquals(2, result.total());
        assertEquals(1, result.correct());
        assertEquals(50.0, result.percentage());
        assertEquals("Matemática", result.disciplines().getFirst().name());
        assertEquals("Funções", result.subjects().getFirst().name());
        assertEquals("Matemática", result.subjects().getFirst().discipline());
        assertTrue(result.questions().getFirst().correct());
        assertFalse(result.questions().get(1).correct());
        assertEquals("A", result.questions().get(1).correctLetter());
    }

    @Test
    void rejectsAnswerFromAnotherQuestion() {
        var practice = ownedPractice(DirectedPracticeStatus.EM_ANDAMENTO);
        var question = new DirectedPracticeQuestionEntity();
        question.setId(UUID.randomUUID());
        practice.getQuestions().add(question);
        assertThrows(BadRequestException.class,
                () -> service.answer(practice.getId(), question.getId(), UUID.randomUUID()));
        verify(practices, never()).save(any());
    }

    @Test
    void refusesToFinishUnansweredPractice() {
        var practice = ownedPractice(DirectedPracticeStatus.EM_ANDAMENTO);
        assertThrows(BadRequestException.class, () -> service.finish(practice.getId()));
        assertEquals(DirectedPracticeStatus.EM_ANDAMENTO, practice.getStatus());
    }

    @Test
    void finishFreezesIndividualCorrectionsOnlyAfterTwentyAnswers() {
        var practice = ownedPractice(DirectedPracticeStatus.EM_ANDAMENTO);
        for (int position = 1; position <= 20; position++) {
            var question = reviewQuestion(position, position % 2 == 0);
            question.setCorrectAtFinish(null);
            practice.getQuestions().add(question);
        }
        when(practices.save(practice)).thenReturn(practice);

        var result = service.finish(practice.getId());

        assertEquals(DirectedPracticeStatus.FINALIZADA, result.status());
        assertNotNull(practice.getFinishedAt());
        assertEquals(10, practice.getQuestions().stream().filter(item -> Boolean.TRUE.equals(item.getCorrectAtFinish())).count());
        assertEquals(10, practice.getQuestions().stream().filter(item -> Boolean.FALSE.equals(item.getCorrectAtFinish())).count());
    }

    @Test
    void cancellationKeepsPracticeAndBlocksFurtherAnswers() {
        var practice = ownedPractice(DirectedPracticeStatus.EM_ANDAMENTO);
        when(practices.save(practice)).thenReturn(practice);
        var result = service.cancel(practice.getId());
        assertEquals(DirectedPracticeStatus.CANCELADA, result.status());
        assertNotNull(practice.getFinishedAt());
        assertThrows(ConflictException.class,
                () -> service.answer(practice.getId(), UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void cannotReadOtherStudentsPractice() {
        UUID id = UUID.randomUUID();
        when(actor.getCurrentUserId()).thenReturn(studentId);
        assertThrows(NotFoundException.class, () -> service.findById(id));
        verify(practices).findByIdAndStudent_Id(id, studentId);
    }

    private DirectedPracticeEntity ownedPractice(DirectedPracticeStatus status) {
        var practice = new DirectedPracticeEntity();
        practice.setId(UUID.randomUUID());
        practice.setStatus(status);
        when(actor.getCurrentUserId()).thenReturn(studentId);
        when(practices.findByIdAndStudent_Id(practice.getId(), studentId)).thenReturn(Optional.of(practice));
        return practice;
    }

    private QuestionEntity question(ExamAreaEnum area) {
        var discipline = new DisciplineEntity();
        discipline.setExamArea(area);
        discipline.setName(area.name());
        var subject = new SubjectEntity();
        subject.setName("Assunto");
        subject.setDiscipline(discipline);
        var question = new QuestionEntity();
        question.setId(UUID.randomUUID());
        question.setStatement(question.getId().toString());
        question.setExplanation("Explicação");
        question.setSubject(subject);
        for (int i = 0; i < 2; i++) {
            var alternative = new AlternativeEntity();
            alternative.setId(UUID.randomUUID());
            alternative.setQuestion(question);
            alternative.setLetter(i == 0 ? "A" : "B");
            alternative.setText("Opção " + i);
            alternative.setCorrect(i == 0);
            question.getAlternatives().add(alternative);
        }
        return question;
    }

    private DirectedPracticeQuestionEntity reviewQuestion(int position, boolean correct) {
        var question = new DirectedPracticeQuestionEntity();
        question.setId(UUID.randomUUID());
        question.setPosition(position);
        question.setStatement("Enunciado " + position);
        question.setExplanation("Explicação histórica");
        question.setDisciplineName("Matemática");
        question.setSubjectName("Funções");
        question.setCorrectAtFinish(correct);
        for (int i = 0; i < 2; i++) {
            var alternative = new DirectedPracticeAlternativeEntity();
            alternative.setId(UUID.randomUUID());
            alternative.setLetter(i == 0 ? "A" : "B");
            alternative.setText("Opção " + i);
            alternative.setCorrect(i == 0);
            question.getAlternatives().add(alternative);
        }
        question.setSelectedAlternativeId(question.getAlternatives().get(correct ? 0 : 1).getId());
        return question;
    }
}
