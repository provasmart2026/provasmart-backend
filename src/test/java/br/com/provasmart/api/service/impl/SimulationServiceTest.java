package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationAnswerEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.request.simulation.SimulationAnswerRequestDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.simulation.ISimulationAlternativeMapper;
import br.com.provasmart.api.mapper.simulation.ISimulationAnswerMapper;
import br.com.provasmart.api.mapper.simulation.ISimulationMapper;
import br.com.provasmart.api.mapper.simulation.ISimulationQuestionMapper;
import br.com.provasmart.api.repository.questions.IAlternativeRepository;
import br.com.provasmart.api.repository.questions.IQuestionRepository;
import br.com.provasmart.api.repository.simulations.ISimulationAnswerRepository;
import br.com.provasmart.api.repository.simulations.ISimulationQuestionRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock private ISimulationRepository simulationRepository;
    @Mock private ISimulationQuestionRepository simulationQuestionRepository;
    @Mock private ISimulationAnswerRepository simulationAnswerRepository;
    @Mock private IQuestionRepository questionRepository;
    @Mock private IAlternativeRepository alternativeRepository;
    @Mock private IUserRepository userRepository;
    @Mock private ICurrentActorService currentActorService;
    @Spy private ISimulationMapper simulationMapper = Mappers.getMapper(ISimulationMapper.class);
    @Spy private ISimulationQuestionMapper simulationQuestionMapper = Mappers.getMapper(ISimulationQuestionMapper.class);
    @Spy private ISimulationAnswerMapper simulationAnswerMapper = Mappers.getMapper(ISimulationAnswerMapper.class);
    @InjectMocks private SimulationService simulationService;

    private final UUID studentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(simulationMapper, "iSimulationQuestionMapper", simulationQuestionMapper);
        ReflectionTestUtils.setField(simulationQuestionMapper, "iSimulationAlternativeMapper",
                Mappers.getMapper(ISimulationAlternativeMapper.class));
        when(currentActorService.getCurrentUserId()).thenReturn(studentId);
    }

    @Test
    void createsAnInProgressSimulationWithTenQuestionsPerArea() {
        allowStudentLookup();
        for (var area : ExamAreaEnum.values()) {
            when(questionRepository.findAllByActiveTrueAndSubjectDisciplineExamArea(area))
                    .thenReturn(questionsFor(area, 12));
        }
        var simulationId = UUID.randomUUID();
        when(simulationRepository.save(any(SimulationEntity.class))).thenAnswer(invocation -> {
            SimulationEntity simulation = invocation.getArgument(0);
            simulation.setId(simulationId);
            return simulation;
        });

        var result = simulationService.create();

        var captor = ArgumentCaptor.forClass(SimulationEntity.class);
        verify(simulationRepository).save(captor.capture());
        var questions = captor.getValue().getQuestions();
        assertEquals(40, questions.size());
        assertEquals(40L, questions.stream().map(question -> question.getQuestion().getId()).distinct().count());
        assertEquals(IntStream.rangeClosed(1, 40).boxed().toList(),
                questions.stream().map(SimulationQuestionEntity::getPosition).toList());
        for (var area : ExamAreaEnum.values()) {
            assertEquals(10L, questions.stream().filter(question -> question.getExamAreaSnapshot() == area).count());
        }
        assertEquals(simulationId, result.id());
        assertEquals(studentId, result.studentId());
        assertEquals(SimulationStatusEnum.EM_ANDAMENTO, result.status());
        assertNotNull(result.startedAt());
        assertNull(result.finishedAt());
        assertEquals(40, result.questions().size());
    }

    @Test
    void rejectsCreationWhenTheStudentAlreadyHasAnInProgressSimulation() {
        allowStudentLookup();
        when(simulationRepository.existsByStudent_IdAndStatus(studentId, SimulationStatusEnum.EM_ANDAMENTO))
                .thenReturn(true);

        var error = assertThrows(ConflictException.class, simulationService::create);

        assertEquals("Já existe um simulado em andamento para este estudante.", error.getMessage());
        verifyNoInteractions(questionRepository);
        verify(simulationRepository, never()).save(any());
    }

    @Test
    void rejectsCreationWithoutSavingWhenAnAreaHasTooFewQuestions() {
        allowStudentLookup();
        var insufficientArea = ExamAreaEnum.MATEMATICA;
        for (var area : ExamAreaEnum.values()) {
            when(questionRepository.findAllByActiveTrueAndSubjectDisciplineExamArea(area))
                    .thenReturn(questionsFor(area, area == insufficientArea ? 9 : 10));
        }

        var error = assertThrows(ConflictException.class, simulationService::create);

        assertEquals("Não há questões suficientes para a área de prova: " + insufficientArea, error.getMessage());
        verify(simulationRepository, never()).save(any());
    }

    @Test
    void recordsTheFirstAnswerAndReturnsTheSelectedAlternative() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        var question = addQuestion(simulation);
        var selected = question.getQuestion().getAlternatives().getFirst();
        allowAnswerSelection(question, selected);
        when(simulationAnswerRepository.findBySimulationQuestionId(question.getId())).thenReturn(Optional.empty());
        when(simulationAnswerRepository.save(any(SimulationAnswerEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = simulationService.answerQuestion(simulation.getId(), question.getId(),
                new SimulationAnswerRequestDTO(selected.getId()));

        var captor = ArgumentCaptor.forClass(SimulationAnswerEntity.class);
        verify(simulationAnswerRepository).save(captor.capture());
        var answer = captor.getValue();
        assertSame(question, answer.getSimulationQuestion());
        assertSame(selected, answer.getSelectedAlternative());
        assertNotNull(answer.getAnsweredAt());
        assertNull(answer.getIsCorrect());
        assertEquals(selected.getId(), result.questions().getFirst().selectedAlternativeId());
    }

    @Test
    void updatesTheExistingAnswerWithoutChangingItsId() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        var question = addQuestion(simulation);
        var previous = question.getQuestion().getAlternatives().getFirst();
        var selected = new AlternativeEntity();
        selected.setId(UUID.randomUUID());
        selected.setLetter("B");
        selected.setText("Outra alternativa");
        selected.setQuestion(question.getQuestion());
        question.getQuestion().setAlternatives(List.of(previous, selected));
        var answerId = UUID.randomUUID();
        var existing = new SimulationAnswerEntity();
        existing.setId(answerId);
        existing.setSimulationQuestion(question);
        existing.setSelectedAlternative(previous);
        question.setAnswer(existing);
        allowAnswerSelection(question, selected);
        when(simulationAnswerRepository.findBySimulationQuestionId(question.getId())).thenReturn(Optional.of(existing));
        when(simulationAnswerRepository.save(existing)).thenReturn(existing);

        var result = simulationService.answerQuestion(simulation.getId(), question.getId(),
                new SimulationAnswerRequestDTO(selected.getId()));

        verify(simulationAnswerRepository).save(existing);
        assertEquals(answerId, existing.getId());
        assertSame(selected, existing.getSelectedAlternative());
        assertEquals(selected.getId(), result.questions().getFirst().selectedAlternativeId());
    }

    @ParameterizedTest
    @EnumSource(value = SimulationStatusEnum.class, names = {"FINALIZADO", "CANCELADO"})
    void rejectsAnswersToSimulationsThatAreNotInProgress(SimulationStatusEnum status) {
        var simulation = allowSimulationForUpdate(status);
        var request = new SimulationAnswerRequestDTO(UUID.randomUUID());

        var error = assertThrows(ConflictException.class,
                () -> simulationService.answerQuestion(simulation.getId(), UUID.randomUUID(), request));

        assertEquals("O simulado não está em andamento.", error.getMessage());
        verifyNoInteractions(simulationQuestionRepository, alternativeRepository, simulationAnswerRepository);
    }

    @Test
    void rejectsAQuestionFromAnotherSimulationBeforeLookingUpTheAlternative() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        var otherSimulation = new SimulationEntity();
        otherSimulation.setId(UUID.randomUUID());
        var question = addQuestion(otherSimulation);
        when(simulationQuestionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        var request = new SimulationAnswerRequestDTO(UUID.randomUUID());

        var error = assertThrows(BadRequestException.class,
                () -> simulationService.answerQuestion(simulation.getId(), question.getId(), request));

        assertEquals("A questão não pertence a este simulado.", error.getMessage());
        verifyNoInteractions(alternativeRepository, simulationAnswerRepository);
    }

    @Test
    void rejectsAnAlternativeFromAnotherQuestionWithoutSavingAnAnswer() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        var question = addQuestion(simulation);
        var otherQuestion = questionsFor(ExamAreaEnum.LINGUAGENS, 1).getFirst();
        var alternative = otherQuestion.getAlternatives().getFirst();
        allowAnswerSelection(question, alternative);

        var error = assertThrows(BadRequestException.class,
                () -> simulationService.answerQuestion(simulation.getId(), question.getId(),
                        new SimulationAnswerRequestDTO(alternative.getId())));

        assertEquals("A alternativa não pertence à questão do simulado.", error.getMessage());
        verifyNoInteractions(simulationAnswerRepository);
    }

    @Test
    void rejectsCreationWhenTheStudentDoesNotExist() {
        when(userRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, simulationService::create);

        verifyNoInteractions(simulationRepository, questionRepository);
    }

    @Test
    void rejectsAnAnswerWhenTheSimulationDoesNotBelongToTheStudent() {
        var simulationId = UUID.randomUUID();
        when(simulationRepository.findByIdAndStudentIdForUpdate(simulationId, studentId))
                .thenReturn(Optional.empty());
        var request = new SimulationAnswerRequestDTO(UUID.randomUUID());

        assertThrows(NotFoundException.class,
                () -> simulationService.answerQuestion(simulationId, UUID.randomUUID(), request));

        verifyNoInteractions(simulationQuestionRepository, alternativeRepository, simulationAnswerRepository);
    }

    @Test
    void rejectsAnAnswerWhenTheSimulationQuestionDoesNotExist() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        var questionId = UUID.randomUUID();
        when(simulationQuestionRepository.findById(questionId)).thenReturn(Optional.empty());
        var request = new SimulationAnswerRequestDTO(UUID.randomUUID());

        var error = assertThrows(NotFoundException.class,
                () -> simulationService.answerQuestion(simulation.getId(), questionId, request));

        assertEquals("Questão do simulado não encontrada.", error.getMessage());
        verifyNoInteractions(alternativeRepository, simulationAnswerRepository);
    }

    @Test
    void rejectsAnAnswerWhenTheSelectedAlternativeDoesNotExist() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        var question = addQuestion(simulation);
        var alternativeId = UUID.randomUUID();
        when(simulationQuestionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        when(alternativeRepository.findById(alternativeId)).thenReturn(Optional.empty());

        var error = assertThrows(NotFoundException.class,
                () -> simulationService.answerQuestion(simulation.getId(), question.getId(),
                        new SimulationAnswerRequestDTO(alternativeId)));

        assertEquals("Alternativa não encontrada.", error.getMessage());
        verifyNoInteractions(simulationAnswerRepository);
    }

    @Test
    void finishesTheSimulationUsingThePreservedAnswerKey() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        addAnsweredQuestions(simulation, 40);
        var question = simulation.getQuestions().getLast();
        var selected = question.getAnswer().getSelectedAlternative();
        var preservedCorrectAlternative = new AlternativeEntity();
        preservedCorrectAlternative.setId(UUID.randomUUID());
        preservedCorrectAlternative.setLetter("B");
        preservedCorrectAlternative.setText("Gabarito preservado");
        question.getQuestion().setAlternatives(List.of(selected, preservedCorrectAlternative));
        question.setCorrectAlternativeId(preservedCorrectAlternative.getId());
        when(simulationRepository.save(simulation)).thenReturn(simulation);

        var result = simulationService.finishSimulation(simulation.getId());

        verify(simulationRepository).save(simulation);
        assertEquals(SimulationStatusEnum.FINALIZADO, result.status());
        assertNotNull(result.finishedAt());
        assertEquals(39L, simulation.getQuestions().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAnswer().getIsCorrect()))
                .count());
        assertEquals(Boolean.FALSE, question.getAnswer().getIsCorrect());
        assertEquals(selected.getId(), result.questions().getLast().selectedAlternativeId());
    }

    @ParameterizedTest
    @CsvSource({"39,false", "40,true"})
    void refusesToFinishAnIncompleteSimulation(int totalQuestions, boolean removeAnswer) {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        addAnsweredQuestions(simulation, totalQuestions);
        if (removeAnswer) simulation.getQuestions().getLast().setAnswer(null);

        var error = assertThrows(BadRequestException.class,
                () -> simulationService.finishSimulation(simulation.getId()));

        assertEquals("Todas as questões devem ser respondidas antes de finalizar o simulado.", error.getMessage());
        assertEquals(SimulationStatusEnum.EM_ANDAMENTO, simulation.getStatus());
        assertNull(simulation.getFinishedAt());
        verify(simulationRepository, never()).save(any());
    }

    @Test
    void refusesToFinishWhenAnAnswerHasNoSelectedAlternative() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        addAnsweredQuestions(simulation, 40);
        simulation.getQuestions().getLast().getAnswer().setSelectedAlternative(null);

        assertThrows(BadRequestException.class, () -> simulationService.finishSimulation(simulation.getId()));

        verify(simulationRepository, never()).save(any());
    }

    @Test
    void refusesToFinishWithoutThePreservedAnswerKey() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        addAnsweredQuestions(simulation, 40);
        simulation.getQuestions().getLast().setCorrectAlternativeId(null);

        var error = assertThrows(ConflictException.class, () -> simulationService.finishSimulation(simulation.getId()));

        assertEquals("O simulado não possui gabarito preservado e não pode ser corrigido automaticamente.",
                error.getMessage());
        assertNull(simulation.getQuestions().getFirst().getAnswer().getIsCorrect());
        assertEquals(SimulationStatusEnum.EM_ANDAMENTO, simulation.getStatus());
        verify(simulationRepository, never()).save(any());
    }

    @Test
    void cancelsAnInProgressSimulationAndKeepsItsAnswers() {
        var simulation = allowSimulationForUpdate(SimulationStatusEnum.EM_ANDAMENTO);
        addAnsweredQuestions(simulation, 1);
        var answer = simulation.getQuestions().getFirst().getAnswer();
        when(simulationRepository.save(simulation)).thenReturn(simulation);

        var result = simulationService.cancelSimulation(simulation.getId());

        verify(simulationRepository).save(simulation);
        verifyNoInteractions(simulationAnswerRepository);
        assertEquals(SimulationStatusEnum.CANCELADO, result.status());
        assertSame(answer, simulation.getQuestions().getFirst().getAnswer());
        assertEquals(answer.getSelectedAlternative().getId(), result.questions().getFirst().selectedAlternativeId());
    }

    @ParameterizedTest
    @EnumSource(value = SimulationStatusEnum.class, names = {"FINALIZADO", "CANCELADO"})
    void cannotFinishOrCancelASimulationThatIsAlreadyClosed(SimulationStatusEnum status) {
        var simulation = allowSimulationForUpdate(status);

        assertThrows(ConflictException.class, () -> simulationService.finishSimulation(simulation.getId()));
        assertThrows(ConflictException.class, () -> simulationService.cancelSimulation(simulation.getId()));

        assertEquals(status, simulation.getStatus());
        verify(simulationRepository, never()).save(any());
    }

    @Test
    void findsASimulationForTheCurrentStudent() {
        var simulation = simulation(SimulationStatusEnum.EM_ANDAMENTO);
        when(simulationRepository.findByIdAndStudent_Id(simulation.getId(), studentId))
                .thenReturn(Optional.of(simulation));

        var result = simulationService.findById(simulation.getId());

        assertEquals(simulation.getId(), result.id());
        assertEquals(studentId, result.studentId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void findsTheCurrentSimulationOnlyWhenOneIsInProgress(boolean exists) {
        var simulation = simulation(SimulationStatusEnum.EM_ANDAMENTO);
        when(simulationRepository.findByStudent_IdAndStatus(studentId, SimulationStatusEnum.EM_ANDAMENTO))
                .thenReturn(exists ? Optional.of(simulation) : Optional.empty());

        var result = simulationService.findCurrent();

        if (exists) assertEquals(simulation.getId(), result.orElseThrow().id());
        else assertEquals(Optional.empty(), result);
    }

    @Test
    void findsTheLatestFinishedSimulationForTheCurrentStudent() {
        var simulation = simulation(SimulationStatusEnum.FINALIZADO);
        when(simulationRepository.findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(
                studentId, SimulationStatusEnum.FINALIZADO)).thenReturn(Optional.of(simulation));

        var result = simulationService.findLatestFinished();

        assertEquals(simulation.getId(), result.id());
        assertEquals(SimulationStatusEnum.FINALIZADO, result.status());
    }

    @Test
    void reportsWhenTheCurrentStudentHasNoFinishedSimulation() {
        when(simulationRepository.findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(
                studentId, SimulationStatusEnum.FINALIZADO)).thenReturn(Optional.empty());

        var error = assertThrows(NotFoundException.class, simulationService::findLatestFinished);

        assertEquals("Nenhum Simulado Geral finalizado foi encontrado.", error.getMessage());
    }

    @Test
    void historyShowsScoresOnlyForFinishedSimulationsAndPreservesPagination() {
        var finished = simulation(SimulationStatusEnum.FINALIZADO);
        addAnsweredQuestions(finished, 2);
        finished.getQuestions().getFirst().getAnswer().setIsCorrect(true);
        finished.getQuestions().getLast().getAnswer().setIsCorrect(false);
        var cancelled = simulation(SimulationStatusEnum.CANCELADO);
        addAnsweredQuestions(cancelled, 1);
        addQuestion(cancelled);
        var legacy = simulation(SimulationStatusEnum.FINALIZADO);
        addQuestion(legacy);
        var pageable = PageRequest.of(1, 3);
        when(simulationRepository.findByStudent_IdAndStatusInOrderByStartedAtDescIdDesc(
                studentId, List.of(SimulationStatusEnum.FINALIZADO, SimulationStatusEnum.CANCELADO), pageable))
                .thenReturn(new PageImpl<>(List.of(finished, cancelled, legacy), pageable, 6));

        var result = simulationService.findHistory(pageable);

        assertEquals(6, result.getTotalElements());
        assertEquals(1, result.getNumber());
        var finishedSummary = result.getContent().getFirst();
        assertEquals(finished.getId(), finishedSummary.id());
        assertEquals(2, finishedSummary.answeredQuestions());
        assertEquals(1, finishedSummary.correctAnswers());
        assertEquals(1, finishedSummary.incorrectAnswers());
        assertEquals(50.0, finishedSummary.accuracyPercentage());
        var cancelledSummary = result.getContent().get(1);
        assertEquals(cancelled.getId(), cancelledSummary.id());
        assertEquals(2, cancelledSummary.totalQuestions());
        assertEquals(1, cancelledSummary.answeredQuestions());
        assertNull(cancelledSummary.correctAnswers());
        assertNull(cancelledSummary.incorrectAnswers());
        assertNull(cancelledSummary.accuracyPercentage());
        assertEquals(0.0, result.getContent().getLast().accuracyPercentage());
    }

    private void allowStudentLookup() {
        var student = new UserEntity();
        student.setId(studentId);
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
    }

    private SimulationEntity allowSimulationForUpdate(SimulationStatusEnum status) {
        var simulation = simulation(status);
        when(simulationRepository.findByIdAndStudentIdForUpdate(simulation.getId(), studentId))
                .thenReturn(Optional.of(simulation));
        return simulation;
    }

    private SimulationEntity simulation(SimulationStatusEnum status) {
        var student = new UserEntity();
        student.setId(studentId);
        var simulation = new SimulationEntity();
        simulation.setId(UUID.randomUUID());
        simulation.setStudent(student);
        simulation.setStatus(status);
        simulation.setStartedAt(LocalDateTime.of(2026, 11, 20, 12, 0));
        return simulation;
    }

    private void addAnsweredQuestions(SimulationEntity simulation, int count) {
        for (int index = 0; index < count; index++) {
            var question = addQuestion(simulation);
            var answer = new SimulationAnswerEntity();
            answer.setSimulationQuestion(question);
            answer.setSelectedAlternative(question.getQuestion().getAlternatives().getFirst());
            question.setAnswer(answer);
        }
    }

    private SimulationQuestionEntity addQuestion(SimulationEntity simulation) {
        var original = questionsFor(ExamAreaEnum.LINGUAGENS, 1).getFirst();
        var question = simulationQuestionMapper.toEntity(simulation, original, simulation.getQuestions().size() + 1);
        question.setId(UUID.randomUUID());
        simulation.getQuestions().add(question);
        return question;
    }

    private void allowAnswerSelection(SimulationQuestionEntity question, AlternativeEntity alternative) {
        when(simulationQuestionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        when(alternativeRepository.findById(alternative.getId())).thenReturn(Optional.of(alternative));
    }

    private List<QuestionEntity> questionsFor(ExamAreaEnum area, int count) {
        var discipline = new DisciplineEntity();
        discipline.setId(UUID.randomUUID());
        discipline.setName(area.name());
        discipline.setExamArea(area);
        var subject = new SubjectEntity();
        subject.setId(UUID.randomUUID());
        subject.setName("Assunto de teste");
        subject.setDiscipline(discipline);

        return IntStream.rangeClosed(1, count).mapToObj(number -> {
            var question = new QuestionEntity();
            question.setId(UUID.randomUUID());
            question.setStatement("Questão " + number);
            question.setSubject(subject);
            question.setActive(true);

            var correct = new AlternativeEntity();
            correct.setId(UUID.randomUUID());
            correct.setLetter("A");
            correct.setText("Alternativa correta");
            correct.setCorrect(true);
            correct.setQuestion(question);
            question.setAlternatives(List.of(correct));
            return question;
        }).toList();
    }
}
