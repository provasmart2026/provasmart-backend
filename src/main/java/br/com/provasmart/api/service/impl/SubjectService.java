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
import br.com.provasmart.api.service.ISubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubjectService implements ISubjectService {

    private final ISubjectRepository subjectRepository;
    private final ISubjectMapper subjectMapper;
    private final IDisciplineService disciplineService;

    @Override
    public List<SubjectResponseDTO> findAllByDiscipline(UUID disciplineId) {
        log.info("Finding all subjects for disciplineId: {}", disciplineId);
        var subjects = getAllByDiscipline(disciplineId);
        return getList(subjects);
    }

    @Override
    public SubjectResponseDTO create(UUID disciplineId, SubjectRequestDTO subjectRequestDTO) {
        log.info("Creating subject for disciplineId: {}", disciplineId);
        var discipline = getDisciplineEntity(disciplineId);
        validateSubjectExist(disciplineId, subjectRequestDTO.name());
        var subjectEntity = mapToEntity(discipline, subjectRequestDTO);
        save(subjectEntity);
        return mapToResponseDTO(subjectEntity);
    }

    @Override
    public SubjectEntity findById(UUID subjectId) {
        log.info("Finding subject by id: {}", subjectId);
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> {
                    log.error("Subject not found for id: {}", subjectId);
                    return new NotFoundException("Assunto não encontrado.");
                });
    }

    private SubjectEntity mapToEntity(DisciplineEntity discipline, SubjectRequestDTO subjectRequestDTO) {
        log.info("Mapping SubjectRequestDTO to SubjectEntity for disciplineId: {}", discipline.getId());
        return subjectMapper.toEntity(discipline, subjectRequestDTO);
    }

    private SubjectResponseDTO mapToResponseDTO(SubjectEntity subjectEntity) {
        log.info("Mapping SubjectEntity to SubjectResponseDTO for subject: {}", subjectEntity);
        return subjectMapper.toResponseDTO(subjectEntity);
    }

    private void save(SubjectEntity subjectEntity) {
        log.info("Saving subject entity: {}", subjectEntity);
        subjectRepository.save(subjectEntity);
    }

    private DisciplineEntity getDisciplineEntity(UUID id) {
        log.info("Fetching discipline entity for id: {}", id);
        return disciplineService.findById(id);
    }

    private List<SubjectResponseDTO> getList(List<SubjectEntity> subjects) {
        log.info("Mapping list of SubjectEntity to SubjectResponseDTO");
        return subjects.stream().map(subjectMapper::toResponseDTO).toList();
    }

    private List<SubjectEntity> getAllByDiscipline(UUID disciplineId) {
        log.info("Fetching all subjects for disciplineId: {}", disciplineId);
        return subjectRepository.findAllByDiscipline_IdOrderByNameAsc(disciplineId);
    }

    private void validateSubjectExist(UUID disciplineId, String name) {
        var alreadyExists = subjectRepository.existsByNameIgnoreCaseAndDiscipline_Id(name, disciplineId);

        if (alreadyExists) {
            log.error("Subject with name '{}' already exists for disciplineId: {}", name, disciplineId);
            throw new ConflictException("Já existe um assunto com este nome para esta disciplina.");
        }
    }
}
