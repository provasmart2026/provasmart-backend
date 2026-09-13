package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
import br.com.provasmart.api.dto.request.question.QuestionRequestDTO;
import br.com.provasmart.api.dto.response.question.QuestionResponseDTO;
import br.com.provasmart.api.mapper.question.IAlternativeMapper;
import br.com.provasmart.api.mapper.question.IQuestionMapper;
import br.com.provasmart.api.repository.questions.IQuestionRepository;
import br.com.provasmart.api.service.IQuestionService;
import br.com.provasmart.api.service.ISubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class QuestionService implements IQuestionService {

    private final IQuestionRepository questionRepository;
    private final ISubjectService subjectService;
    private final IQuestionMapper questionMapper;
    private final IAlternativeMapper alternativeMapper;

    @Override
    public QuestionResponseDTO create(QuestionRequestDTO requestDTO) {
        log.info("Creating question with subjectId: {}", requestDTO.subjectId());
        validateCorrectAlternative(requestDTO);
        validateAlternativeLetters(requestDTO);
        var subject = subjectService.findById(requestDTO.subjectId());
        var questionEntity = mapToEntity(requestDTO, subject);
        save(questionEntity);
        return mapToDTO(questionEntity);
    }

    @Override
    public QuestionResponseDTO update(UUID id, QuestionRequestDTO requestDTO) {
        log.info("Updating question with id: {}", id);
        validateCorrectAlternative(requestDTO);
        validateAlternativeLetters(requestDTO);
        var questionEntity = getQuestionEntity(id);
        var subject = subjectService.findById(requestDTO.subjectId());

        mapToEntityUpdate(requestDTO, subject, questionEntity);
        updateAlternatives(requestDTO, questionEntity);

        save(questionEntity);
        return mapToDTO(questionEntity);
    }

    @Override
    public void deactivate(UUID id) {
        log.info("Deactivating question with id: {}", id);
        var questionEntity = getQuestionEntity(id);
        questionEntity.setActive(false);
        questionEntity.setUpdatedAt(LocalDateTime.now());
        save(questionEntity);
    }

    @Override
    public void activate(UUID id) {
        log.info("Activating question with id: {}", id);
        var questionEntity = getQuestionEntity(id);
        questionEntity.setActive(true);
        questionEntity.setUpdatedAt(LocalDateTime.now());
        save(questionEntity);
    }

    @Override
    public QuestionResponseDTO findById(UUID id) {
        log.info("Finding question by id: {}", id);
        var questionEntity = getQuestionEntity(id);
        return mapToDTO(questionEntity);
    }

    @Override
    public Page<QuestionResponseDTO> findAll(Pageable pageable) {
        log.info("Finding all questions with pageable: {}", pageable);
        return questionRepository.findAllByOrderByActiveDesc(pageable).map(this::mapToDTO);
    }

    @Override
    public Page<QuestionResponseDTO> findAllActive(Pageable pageable) {
        log.info("Finding all active questions with pageable: {}", pageable);
        return questionRepository.findAllByActiveTrue(pageable).map(this::mapToDTO);
    }

    private QuestionEntity mapToEntity(QuestionRequestDTO requestDTO, SubjectEntity subject) {
        log.info("Converting QuestionRequestDTO to QuestionEntity: {}", requestDTO);
        return questionMapper.toEntity(requestDTO, subject);
    }

    private void save(QuestionEntity questionEntity) {
        log.info("Saving QuestionEntity: {}", questionEntity);
        questionRepository.save(questionEntity);
    }

    private QuestionResponseDTO mapToDTO(QuestionEntity questionEntity) {
        log.info("Converting QuestionEntity to QuestionResponseDTO: {}", questionEntity);
        return questionMapper.toDTO(questionEntity);
    }

    private void validateCorrectAlternative(QuestionRequestDTO requestDTO) {
        log.info("Validating correct alternative");

        var correctAlternatives = requestDTO.alternatives()
                .stream()
                .filter(alternative -> alternative.correct())
                .count();

        if (correctAlternatives != 1) {
            log.error(
                    "Question must have exactly one correct alternative. Found: {}",
                    correctAlternatives
            );

            throw new IllegalArgumentException(
                    "A questão deve possuir exatamente uma alternativa correta"
            );
        }
    }

    private void validateAlternativeLetters(QuestionRequestDTO requestDTO) {
        log.info("Validating alternative letters");

        var expectedLetters = Set.of("A", "B", "C", "D", "E");

        var receivedLetters = requestDTO.alternatives()
                .stream()
                .map(alternative -> alternative.letter().toUpperCase())
                .collect(Collectors.toSet());

        if (!expectedLetters.equals(receivedLetters)) {
            log.error("Alternative letters must be: {}. Found: {}", expectedLetters, receivedLetters);
            throw new IllegalArgumentException(
                    "As letras das alternativas devem ser: A, B, C, D e E"
            );
        }
    }

    private QuestionEntity getQuestionEntity(UUID id) {
        log.info("Fetching question entity for id: {}", id);

        return questionRepository.findOneById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found")
                );
    }

    private void updateAlternatives(QuestionRequestDTO requestDTO, QuestionEntity questionEntity) {
        log.info("Updating alternatives for question with id: {}", questionEntity.getId());
        for (var alternativeRequest : requestDTO.alternatives()) {
            var alternativeEntity = getAlternativeByLetter(questionEntity, alternativeRequest.letter());
            alternativeMapper.toEntityUpdate(alternativeRequest, alternativeEntity);
        }
    }

    private AlternativeEntity getAlternativeByLetter(QuestionEntity questionEntity, String letter) {
        log.info("Fetching alternative for question with id: {} and letter: {}", questionEntity.getId(), letter);
        return questionEntity.getAlternatives().stream()
                .filter(alternative -> alternative.getLetter().equalsIgnoreCase(letter))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Alternative not found for letter: " + letter
                ));
    }

    private void mapToEntityUpdate(QuestionRequestDTO requestDTO, SubjectEntity subject, QuestionEntity questionEntity) {
        log.info("Mapping QuestionRequestDTO to existing QuestionEntity for update: {}", requestDTO);
        questionMapper.toEntityUpdate(requestDTO, subject, questionEntity);
    }
}
