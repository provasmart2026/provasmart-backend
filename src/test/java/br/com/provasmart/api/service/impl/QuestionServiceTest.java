package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import br.com.provasmart.api.dto.request.question.AlternativeRequestDTO;
import br.com.provasmart.api.dto.request.question.QuestionRequestDTO;
import br.com.provasmart.api.dto.response.question.QuestionResponseDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.question.IAlternativeMapper;
import br.com.provasmart.api.mapper.question.IQuestionMapper;
import br.com.provasmart.api.repository.questions.IQuestionRepository;
import br.com.provasmart.api.service.IQuestionReportService;
import br.com.provasmart.api.service.ISubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock private IQuestionRepository questionRepository;
    @Mock private IQuestionReportService questionReportService;
    @Mock private ISubjectService subjectService;
    @Spy private IQuestionMapper questionMapper = Mappers.getMapper(IQuestionMapper.class);
    @Spy private IAlternativeMapper alternativeMapper = Mappers.getMapper(IAlternativeMapper.class);
    @InjectMocks private QuestionService questionService;

    private final UUID questionId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();

    @BeforeEach
    void connectMappers() {
        ReflectionTestUtils.setField(questionMapper, "iAlternativeMapper", alternativeMapper);
    }

    @Test
    void createsAnActiveQuestionOutsideReportReviewWithFiveAlternatives() {
        when(subjectService.findById(subjectId)).thenReturn(subject(subjectId));
        when(questionRepository.save(any(QuestionEntity.class))).thenAnswer(invocation -> {
            QuestionEntity question = invocation.getArgument(0);
            question.setId(questionId);
            return question;
        });

        var result = questionService.create(request("ABCDE", "C"));

        var captor = ArgumentCaptor.forClass(QuestionEntity.class);
        verify(questionRepository).save(captor.capture());
        var saved = captor.getValue();
        assertEquals(questionId, result.id());
        assertEquals(subjectId, result.subjectId());
        assertEquals("Novo enunciado", result.statement());
        assertEquals("Nova explicação", result.explanation());
        assertTrue(result.active());
        assertFalse(saved.isUnderReportReview());
        assertNotNull(result.createdAt());
        assertEquals(List.of("A", "B", "C", "D", "E"),
                saved.getAlternatives().stream().map(AlternativeEntity::getLetter).toList());
        assertEquals(List.of("C"), saved.getAlternatives().stream().filter(AlternativeEntity::isCorrect)
                .map(AlternativeEntity::getLetter).toList());
        saved.getAlternatives().forEach(alternative -> assertSame(saved, alternative.getQuestion()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "AC"})
    void rejectsCreationWithoutExactlyOneCorrectAlternative(String correctLetters) {
        var error = assertThrows(BadRequestException.class,
                () -> questionService.create(request("ABCDE", correctLetters)));

        assertEquals("A questão deve possuir exatamente uma alternativa correta.", error.getMessage());
        verifyNoInteractions(subjectService, questionRepository);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsInvalidAlternativeLettersBeforeCreatingOrEditing(boolean editing) {
        var invalid = request("ABCDF", "A");

        var error = assertThrows(BadRequestException.class, () -> {
            if (editing) questionService.update(questionId, invalid);
            else questionService.create(invalid);
        });

        assertEquals("As letras das alternativas devem ser: A, B, C, D e E.", error.getMessage());
        verifyNoInteractions(subjectService, questionRepository);
    }

    @Test
    void updatesTheQuestionAndItsExistingAlternativesWithoutChangingReviewState() {
        var original = existingQuestion(false, true);
        var createdAt = original.getCreatedAt();
        var alternativesBefore = List.copyOf(original.getAlternatives());
        var idsBefore = alternativesBefore.stream().map(AlternativeEntity::getId).toList();
        var newSubject = subject(subjectId);
        when(questionRepository.findOneById(questionId)).thenReturn(Optional.of(original));
        when(subjectService.findById(subjectId)).thenReturn(newSubject);

        var result = questionService.update(questionId, request("EDCBA", "D"));

        verify(questionRepository).save(original);
        assertEquals(questionId, result.id());
        assertEquals("Novo enunciado", result.statement());
        assertEquals("Nova explicação", result.explanation());
        assertSame(newSubject, original.getSubject());
        assertEquals(createdAt, result.createdAt());
        assertNotNull(result.updatedAt());
        assertFalse(result.active());
        assertTrue(original.isUnderReportReview());
        assertEquals(alternativesBefore, original.getAlternatives());
        assertEquals(idsBefore, original.getAlternatives().stream().map(AlternativeEntity::getId).toList());
        assertEquals("Alternativa atualizada D", original.getAlternatives().get(3).getText());
        assertFalse(original.getAlternatives().getFirst().isCorrect());
        assertTrue(original.getAlternatives().get(3).isCorrect());
    }

    @Test
    void rejectsAnUpdateWhenAnExistingAlternativeIsMissing() {
        var original = existingQuestion(true, false);
        original.setAlternatives(original.getAlternatives().subList(0, 4));
        when(questionRepository.findOneById(questionId)).thenReturn(Optional.of(original));
        when(subjectService.findById(subjectId)).thenReturn(subject(subjectId));

        var error = assertThrows(NotFoundException.class,
                () -> questionService.update(questionId, request("EABCD", "A")));

        assertEquals("Alternativa não encontrada para a letra: E.", error.getMessage());
        verify(questionRepository, never()).save(any());
    }

    @Test
    void activatesAQuestionAndEndsItsReviewAfterCheckingPendingReports() {
        var original = existingQuestion(false, true);
        when(questionRepository.findReportReviewStatusForUpdate(questionId)).thenReturn(Optional.of(true));
        when(questionRepository.findOneById(questionId)).thenReturn(Optional.of(original));

        questionService.activate(questionId);

        var order = inOrder(questionRepository, questionReportService);
        order.verify(questionRepository).findReportReviewStatusForUpdate(questionId);
        order.verify(questionReportService).validateReactivationAllowed(questionId);
        order.verify(questionRepository).findOneById(questionId);
        order.verify(questionRepository).save(original);
        assertTrue(original.isActive());
        assertFalse(original.isUnderReportReview());
        assertNotNull(original.getUpdatedAt());
    }

    @Test
    void keepsTheQuestionInactiveWhenPendingReportsBlockReactivation() {
        when(questionRepository.findReportReviewStatusForUpdate(questionId)).thenReturn(Optional.of(true));
        var conflict = new ConflictException("Ainda há reportes pendentes.");
        doThrow(conflict).when(questionReportService).validateReactivationAllowed(questionId);

        assertSame(conflict, assertThrows(ConflictException.class, () -> questionService.activate(questionId)));

        verify(questionRepository, never()).findOneById(questionId);
        verify(questionRepository, never()).save(any());
    }

    @Test
    void rejectsActivationWhenThereIsNoQuestionToLock() {
        when(questionRepository.findReportReviewStatusForUpdate(questionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> questionService.activate(questionId));

        verifyNoInteractions(questionReportService);
        verify(questionRepository, never()).save(any());
    }

    @Test
    void deactivatesTheQuestionWithoutEndingReportReview() {
        var original = existingQuestion(true, true);
        when(questionRepository.findOneById(questionId)).thenReturn(Optional.of(original));

        questionService.deactivate(questionId);

        verify(questionRepository).save(original);
        assertFalse(original.isActive());
        assertTrue(original.isUnderReportReview());
        assertNotNull(original.getUpdatedAt());
    }

    @Test
    void findsAnInactiveQuestionById() {
        var original = existingQuestion(false, false);
        when(questionRepository.findOneById(questionId)).thenReturn(Optional.of(original));

        var result = questionService.findById(questionId);

        assertEquals(questionId, result.id());
        assertEquals(original.getStatement(), result.statement());
        assertEquals(5, result.alternatives().size());
        assertFalse(result.active());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void reportsWhenTheQuestionToConsultOrEditDoesNotExist(boolean editing) {
        when(questionRepository.findOneById(questionId)).thenReturn(Optional.empty());

        var error = assertThrows(NotFoundException.class, () -> {
            if (editing) questionService.update(questionId, request("ABCDE", "A"));
            else questionService.findById(questionId);
        });

        assertEquals("Questão não encontrada.", error.getMessage());
        verifyNoInteractions(subjectService);
        verify(questionRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void listsTheRequestedQuestionsAndPreservesPagination(boolean activeOnly) {
        var pageable = PageRequest.of(1, 2);
        var original = existingQuestion(activeOnly, false);
        var questions = new PageImpl<>(List.of(original), pageable, 3);
        if (activeOnly) when(questionRepository.findAllByActiveTrue(pageable)).thenReturn(questions);
        else when(questionRepository.findAllByOrderByActiveDesc(pageable)).thenReturn(questions);

        var result = activeOnly ? questionService.findAllActive(pageable) : questionService.findAll(pageable);

        assertEquals(1, result.getNumber());
        assertEquals(3, result.getTotalElements());
        assertEquals(List.of(questionId), result.map(QuestionResponseDTO::id).getContent());
        assertEquals(activeOnly, result.getContent().getFirst().active());
    }

    private QuestionRequestDTO request(String letters, String correctLetters) {
        var alternatives = letters.chars().mapToObj(letter -> new AlternativeRequestDTO(
                Character.toString(letter), "Alternativa atualizada " + (char) letter,
                correctLetters.indexOf(letter) >= 0)).toList();
        return new QuestionRequestDTO("Novo enunciado", "Nova explicação", subjectId, alternatives);
    }

    private QuestionEntity existingQuestion(boolean active, boolean underReview) {
        var original = new QuestionEntity();
        original.setId(questionId);
        original.setStatement("Enunciado original");
        original.setExplanation("Explicação original");
        original.setSubject(subject(UUID.randomUUID()));
        original.setActive(active);
        original.setUnderReportReview(underReview);
        original.setCreatedAt(LocalDateTime.of(2026, 11, 20, 12, 0));
        original.setAlternatives("ABCDE".chars().mapToObj(letter -> {
            var alternative = new AlternativeEntity();
            alternative.setId(UUID.randomUUID());
            alternative.setLetter(Character.toString(letter));
            alternative.setText("Alternativa original " + (char) letter);
            alternative.setCorrect(letter == 'A');
            alternative.setQuestion(original);
            return alternative;
        }).toList());
        return original;
    }

    private SubjectEntity subject(UUID id) {
        var subject = new SubjectEntity();
        subject.setId(id);
        subject.setName("Assunto de teste");
        return subject;
    }
}
