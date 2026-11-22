package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import br.com.provasmart.api.dto.request.question.SubjectRequestDTO;
import br.com.provasmart.api.dto.response.question.SubjectResponseDTO;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.question.ISubjectMapper;
import br.com.provasmart.api.repository.questions.ISubjectRepository;
import br.com.provasmart.api.service.IDisciplineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock private ISubjectRepository repository;
    @Mock private IDisciplineService disciplineService;
    @Spy private ISubjectMapper mapper = Mappers.getMapper(ISubjectMapper.class);
    @InjectMocks private SubjectService service;

    private final UUID disciplineId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();

    @Test
    void listsOnlyTheSelectedDisciplineAndPreservesTheRepositoryOrder() {
        var first = subject("Álgebra");
        var second = subject("Geometria");
        when(repository.findAllByDiscipline_IdOrderByNameAsc(disciplineId)).thenReturn(List.of(first, second));

        var result = service.findAllByDiscipline(disciplineId);

        assertEquals(List.of(first.getId(), second.getId()), result.stream().map(SubjectResponseDTO::id).toList());
        assertEquals(List.of("Álgebra", "Geometria"), result.stream().map(SubjectResponseDTO::name).toList());
    }

    @Test
    void createsTheSubjectUnderTheRequestedDiscipline() {
        var discipline = discipline();
        when(disciplineService.findById(disciplineId)).thenReturn(discipline);
        when(repository.save(any(SubjectEntity.class))).thenAnswer(invocation -> {
            SubjectEntity subject = invocation.getArgument(0);
            subject.setId(subjectId);
            return subject;
        });

        var result = service.create(disciplineId, new SubjectRequestDTO("Geometria"));

        var captor = ArgumentCaptor.forClass(SubjectEntity.class);
        verify(repository).save(captor.capture());
        assertSame(discipline, captor.getValue().getDiscipline());
        assertEquals(new SubjectResponseDTO(subjectId, "Geometria"), result);
    }

    @Test
    void rejectsDuplicateNamesInTheSameDisciplineUsingCaseInsensitiveLookup() {
        when(disciplineService.findById(disciplineId)).thenReturn(discipline());
        when(repository.existsByNameIgnoreCaseAndDiscipline_Id("geometria", disciplineId)).thenReturn(true);

        var error = assertThrows(ConflictException.class,
                () -> service.create(disciplineId, new SubjectRequestDTO("geometria")));

        assertEquals("Já existe um assunto com este nome para esta disciplina.", error.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void doesNotCreateASubjectForAnUnknownDiscipline() {
        when(disciplineService.findById(disciplineId)).thenThrow(new NotFoundException("Disciplina não encontrada."));

        assertThrows(NotFoundException.class, () -> service.create(disciplineId, new SubjectRequestDTO("Geometria")));

        verifyNoInteractions(repository);
    }

    @Test
    void findsAnExistingSubjectById() {
        var expected = subject("Geometria");
        when(repository.findById(expected.getId())).thenReturn(Optional.of(expected));

        assertSame(expected, service.findById(expected.getId()));
    }

    @Test
    void reportsWhenTheSubjectDoesNotExist() {
        when(repository.findById(subjectId)).thenReturn(Optional.empty());

        var error = assertThrows(NotFoundException.class, () -> service.findById(subjectId));

        assertEquals("Assunto não encontrado.", error.getMessage());
    }

    private DisciplineEntity discipline() {
        var discipline = new DisciplineEntity();
        discipline.setId(disciplineId);
        return discipline;
    }

    private SubjectEntity subject(String name) {
        var subject = new SubjectEntity();
        subject.setId(UUID.randomUUID());
        subject.setName(name);
        subject.setDiscipline(discipline());
        return subject;
    }
}
