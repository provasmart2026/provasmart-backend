package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeAlternativeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.practice.DirectedPracticeAnalysisDTO;
import br.com.provasmart.api.dto.practice.DirectedPracticeDTO;
import br.com.provasmart.api.dto.response.practice.DirectedPracticeSummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.mapper.practice.IDirectedPracticeMapper;
import br.com.provasmart.api.repository.practice.IDirectedPracticeRepository;
import br.com.provasmart.api.repository.questions.IQuestionRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import br.com.provasmart.api.service.IDirectedPracticeService;
import br.com.provasmart.api.service.ISimulationPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectedPracticeService implements IDirectedPracticeService {
    private static final int QUESTION_COUNT = 20;

    private final IDirectedPracticeRepository practiceRepository;
    private final ISimulationRepository simulationRepository;
    private final IQuestionRepository questionRepository;
    private final IUserRepository userRepository;
    private final ICurrentActorService currentActorService;
    private final ISimulationPerformanceService simulationPerformanceService;
    private final IDirectedPracticeMapper practiceMapper;

    @Override
    @Transactional
    public DirectedPracticeDTO create() {
        log.info("Creating directed practice");

        var studentId = currentActorService.getCurrentUserId();
        var student = findStudentForUpdate(studentId);
        validateNoPracticeInProgress(studentId);

        var simulation = findLastFinishedSimulation(studentId);
        var area = selectWeakestArea(simulation, studentId);

        var questions = selectQuestions(area);
        var practice = practiceMapper.toEntity(student, simulation, area);
        addQuestions(practice, questions);

        var savedPractice = practiceRepository.saveAndFlush(practice);
        log.info("Directed practice saved; transaction commit pending");
        return toDTO(savedPractice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirectedPracticeDTO> list() {
        log.info("Fetching directed practice history");

        var studentId = currentActorService.getCurrentUserId();
        var practices = practiceRepository.findByStudent_IdOrderByStartedAtDesc(studentId);

        return practices.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DirectedPracticeDTO findById(UUID id) {
        return toDTO(owned(id));
    }

    @Override
    @Transactional
    public DirectedPracticeDTO answer(UUID id, UUID questionId, UUID alternativeId) {
        log.info("Recording answer for directed practice question {}", questionId);

        var practice = owned(id);
        requireInProgress(practice);

        var question = findPracticeQuestion(practice, questionId);
        validateAlternativeBelongsToQuestion(question, alternativeId);

        question.setSelectedAlternativeId(alternativeId);
        question.setAnsweredAt(LocalDateTime.now());

        return toDTO(practiceRepository.save(practice));
    }

    @Override
    @Transactional
    public DirectedPracticeDTO finish(UUID id) {
        log.info("Finishing directed practice with ID {}", id);

        var practice = owned(id);
        requireInProgress(practice);
        validateAllQuestionsAnswered(practice);

        practice.getQuestions().forEach(question -> question.setCorrectAtFinish(isCorrect(question)));
        practice.setStatus(DirectedPracticeStatus.FINALIZADA);
        practice.setFinishedAt(LocalDateTime.now());

        return toDTO(practiceRepository.save(practice));
    }

    @Override
    @Transactional
    public DirectedPracticeDTO cancel(UUID id) {
        log.info("Cancelling directed practice with ID {}", id);

        var practice = owned(id);
        requireInProgress(practice);

        practice.setStatus(DirectedPracticeStatus.CANCELADA);
        practice.setFinishedAt(LocalDateTime.now());

        return toDTO(practiceRepository.save(practice));
    }

    @Override
    @Transactional(readOnly = true)
    public DirectedPracticeAnalysisDTO analysis(UUID id) {
        log.info("Fetching analysis for directed practice with ID {}", id);

        var practice = owned(id);
        validatePracticeFinished(practice);

        var questions = practice.getQuestions();
        var correct = countCorrectAnswers(questions);
        var disciplines = breakdown(questions, DirectedPracticeQuestionEntity::getDisciplineName);
        var subjects = subjectBreakdown(questions);
        var review = questions.stream().map(this::toQuestionReview).toList();

        return new DirectedPracticeAnalysisDTO(
                practice.getId(), practice.getArea(), questions.size(), correct,
                percentage(correct, questions.size()), disciplines, subjects, review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DirectedPracticeSummaryResponseDTO> findHistory(Pageable pageable) {
        var studentId = currentActorService.getCurrentUserId();
        var statuses = List.of(DirectedPracticeStatus.FINALIZADA, DirectedPracticeStatus.CANCELADA);

        log.info("Fetching directed practice history, page {}",
                pageable.isPaged() ? String.valueOf(pageable.getPageNumber()) : "unpaged");
        var practices = practiceRepository.findByStudent_IdAndStatusInOrderByStartedAtDescIdDesc(
                studentId, statuses, pageable);
        return practices.map(this::toHistorySummary);
    }

    private UserEntity findStudentForUpdate(UUID studentId) {
        log.info("Fetching student for update");
        return userRepository.findByIdForUpdate(studentId)
                .orElseThrow(() -> new NotFoundException("Estudante não encontrado."));
    }

    private void validateNoPracticeInProgress(UUID studentId) {
        log.info("Checking for a directed practice in progress");
        if (practiceRepository.existsByStudent_IdAndStatus(studentId, DirectedPracticeStatus.EM_ANDAMENTO)) {
            throw new ConflictException("Já existe uma prática direcionada em andamento.");
        }
    }

    private SimulationEntity findLastFinishedSimulation(UUID studentId) {
        log.info("Fetching the last finished simulation");
        return simulationRepository
                .findFirstByStudent_IdAndStatusOrderByFinishedAtDescIdDesc(studentId, SimulationStatusEnum.FINALIZADO)
                .orElseThrow(() -> new ConflictException("Finalize um Simulado Geral antes de iniciar a prática direcionada."));
    }

    private List<QuestionEntity> selectQuestions(ExamAreaEnum area) {
        log.info("Fetching active questions for area {}", area);
        List<QuestionEntity> pool = questionRepository.findAllByActiveTrueAndSubjectDisciplineExamArea(area);
        Map<UUID, QuestionEntity> distinct = pool.stream()
                .filter(question -> question.getAlternatives().stream().filter(alternative -> alternative.isCorrect()).count() == 1)
                .collect(Collectors.toMap(QuestionEntity::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        if (distinct.size() < QUESTION_COUNT) {
            log.warn("Insufficient eligible questions for area {}: found {}, required {}",
                    area, distinct.size(), QUESTION_COUNT);
            throw new ConflictException("Não há 20 questões ativas disponíveis para a área selecionada.");
        }

        List<QuestionEntity> shuffled = new ArrayList<>(distinct.values());
        Collections.shuffle(shuffled);
        return shuffled.subList(0, QUESTION_COUNT);
    }

    private void addQuestions(DirectedPracticeEntity practice, List<QuestionEntity> questions) {
        log.debug("Adding {} questions to directed practice", questions.size());
        for (int i = 0; i < questions.size(); i++) {
            practice.getQuestions().add(snapshot(practice, questions.get(i), i + 1));
        }
    }

    private DirectedPracticeSummaryResponseDTO toHistorySummary(DirectedPracticeEntity practice) {
        log.debug("Building history summary for directed practice {}", practice.getId());
        var totalQuestions = practice.getQuestions().size();
        var answeredQuestions = findAnsweredQuestions(practice);
        var answeredCount = answeredQuestions.size();
        var finished = practice.getStatus() == DirectedPracticeStatus.FINALIZADA;
        var correctAnswers = finished ? countCorrectAnswers(answeredQuestions) : null;
        var incorrectAnswers = finished ? answeredCount - correctAnswers : null;
        var accuracyPercentage = finished ? percentage(correctAnswers, answeredCount) : null;
        return practiceMapper.toSummaryDTO(practice, totalQuestions, answeredCount,
                correctAnswers, incorrectAnswers, accuracyPercentage);
    }

    private List<DirectedPracticeQuestionEntity> findAnsweredQuestions(DirectedPracticeEntity practice) {
        return practice.getQuestions().stream()
                .filter(question -> question.getSelectedAlternativeId() != null)
                .toList();
    }

    private int countCorrectAnswers(List<DirectedPracticeQuestionEntity> questions) {
        return Math.toIntExact(questions.stream()
                .filter(question -> Boolean.TRUE.equals(question.getCorrectAtFinish()))
                .count());
    }

    private ExamAreaEnum selectWeakestArea(SimulationEntity simulation, UUID studentId) {
        log.info("Fetching performance for simulation with ID {}", simulation.getId());
        return simulationPerformanceService.findById(simulation.getId()).worstAreas().stream().min(Comparator
                .comparing((ExamAreaEnum area) -> practiceRepository.findFirstByStudent_IdAndAreaOrderByStartedAtDesc(studentId, area)
                        .map(DirectedPracticeEntity::getStartedAt).orElse(LocalDateTime.MIN))
                .thenComparingInt(Enum::ordinal))
                .orElseThrow(() -> {
                    log.warn("Simulation {} has no areas for directed practice", simulation.getId());
                    return new ConflictException("O simulado não possui áreas para prática direcionada.");
                });
    }

    private DirectedPracticeQuestionEntity snapshot(DirectedPracticeEntity practice, QuestionEntity original, int position) {
        log.debug("Building directed practice snapshot for question {} at position {}", original.getId(), position);
        if (original.getAlternatives().stream().filter(alternative -> alternative.isCorrect()).count() != 1) {
            log.warn("Question {} has an invalid answer key for directed practice", original.getId());
            throw new ConflictException("Uma questão ativa da área selecionada não possui gabarito válido.");
        }
        var question = practiceMapper.toQuestion(practice, original, position);
        var shuffledAlternatives = new ArrayList<>(original.getAlternatives());
        Collections.shuffle(shuffledAlternatives);
        for (int i = 0; i < shuffledAlternatives.size(); i++) {
            var originalAlternative = shuffledAlternatives.get(i);
            var alternative = practiceMapper.toAlternative(question, originalAlternative, String.valueOf((char) ('A' + i)));
            question.getAlternatives().add(alternative);
        }
        return question;
    }

    private DirectedPracticeEntity owned(UUID id) {
        log.info("Fetching directed practice with ID {}", id);
        return practiceRepository.findByIdAndStudent_Id(id, currentActorService.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Prática direcionada não encontrada."));
    }

    private DirectedPracticeQuestionEntity findPracticeQuestion(DirectedPracticeEntity practice, UUID questionId) {
        log.info("Finding directed practice question with ID {}", questionId);
        return practice.getQuestions().stream()
                .filter(item -> item.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("A questão não pertence a esta prática."));
    }

    private void validateAlternativeBelongsToQuestion(DirectedPracticeQuestionEntity question, UUID alternativeId) {
        log.info("Validating alternative for directed practice question {}", question.getId());
        boolean belongs = question.getAlternatives().stream().anyMatch(item -> item.getId().equals(alternativeId));
        if (!belongs) {
            throw new BadRequestException("A alternativa não pertence a esta questão.");
        }
    }

    private void requireInProgress(DirectedPracticeEntity practice) {
        if (practice.getStatus() != DirectedPracticeStatus.EM_ANDAMENTO) {
            log.warn("Directed practice {} is not in progress, current status {}", practice.getId(), practice.getStatus());
            throw new ConflictException("A prática não está em andamento.");
        }
    }

    private void validateAllQuestionsAnswered(DirectedPracticeEntity practice) {
        var answeredCount = findAnsweredQuestions(practice).size();
        if (practice.getQuestions().size() != QUESTION_COUNT || answeredCount != QUESTION_COUNT) {
            log.warn("Directed practice {} cannot be finished: {} answered, {} total, {} required",
                    practice.getId(), answeredCount, practice.getQuestions().size(), QUESTION_COUNT);
            throw new BadRequestException("Responda as 20 questões antes de finalizar a prática.");
        }
    }

    private DirectedPracticeDTO toDTO(DirectedPracticeEntity practice) {
        return practiceMapper.toDTO(practice);
    }

    private DirectedPracticeDTO.Alternative toAlternative(DirectedPracticeAlternativeEntity item) {
        return practiceMapper.toDTO(item);
    }

    private boolean isCorrect(DirectedPracticeQuestionEntity question) {
        return question.getAlternatives().stream().anyMatch(item -> item.isCorrect()
                && item.getId().equals(question.getSelectedAlternativeId()));
    }

    private void validatePracticeFinished(DirectedPracticeEntity practice) {
        if (practice.getStatus() != DirectedPracticeStatus.FINALIZADA) {
            log.warn("Analysis unavailable for directed practice {} with status {}", practice.getId(), practice.getStatus());
            throw new ConflictException("A correção só fica disponível após a finalização da prática.");
        }
    }

    private DirectedPracticeAnalysisDTO.Review toQuestionReview(DirectedPracticeQuestionEntity question) {
        log.debug("Building review for directed practice question {}", question.getId());
        var right = question.getAlternatives().stream().filter(DirectedPracticeAlternativeEntity::isCorrect)
                .findFirst().orElseThrow();
        var selected = question.getAlternatives().stream()
                .filter(item -> item.getId().equals(question.getSelectedAlternativeId())).findFirst().orElseThrow();
        return new DirectedPracticeAnalysisDTO.Review(question.getId(), question.getPosition(),
                question.getStatement(), question.getExplanation(), question.getDisciplineName(),
                question.getSubjectName(), selected.getId(), right.getId(), selected.getLetter(),
                right.getLetter(), Boolean.TRUE.equals(question.getCorrectAtFinish()),
                question.getAlternatives().stream().map(this::toAlternative).toList());
    }

    private List<DirectedPracticeAnalysisDTO.Breakdown> breakdown(List<DirectedPracticeQuestionEntity> questions,
            Function<DirectedPracticeQuestionEntity, String> grouping) {
        Map<String, List<DirectedPracticeQuestionEntity>> groups = questions.stream()
                .collect(Collectors.groupingBy(grouping, LinkedHashMap::new, Collectors.toList()));
        return groups.entrySet().stream().map(entry -> {
            int correct = (int) entry.getValue().stream().filter(item -> Boolean.TRUE.equals(item.getCorrectAtFinish())).count();
            return new DirectedPracticeAnalysisDTO.Breakdown(entry.getKey(), null, entry.getValue().size(), correct,
                    percentage(correct, entry.getValue().size()));
        }).sorted(Comparator.comparingDouble(DirectedPracticeAnalysisDTO.Breakdown::percentage)
                .thenComparing(DirectedPracticeAnalysisDTO.Breakdown::name)).toList();
    }

    private record SubjectKey(String discipline, String subject) {}

    private List<DirectedPracticeAnalysisDTO.Breakdown> subjectBreakdown(List<DirectedPracticeQuestionEntity> questions) {
        Map<SubjectKey, List<DirectedPracticeQuestionEntity>> groups = questions.stream().collect(Collectors.groupingBy(
                question -> new SubjectKey(question.getDisciplineName(), question.getSubjectName()),
                LinkedHashMap::new, Collectors.toList()));
        return groups.entrySet().stream().map(entry -> {
            int correct = (int) entry.getValue().stream()
                    .filter(item -> Boolean.TRUE.equals(item.getCorrectAtFinish())).count();
            return new DirectedPracticeAnalysisDTO.Breakdown(entry.getKey().subject(), entry.getKey().discipline(),
                    entry.getValue().size(), correct, percentage(correct, entry.getValue().size()));
        }).sorted(Comparator.comparingDouble(DirectedPracticeAnalysisDTO.Breakdown::percentage)
                .thenComparing(DirectedPracticeAnalysisDTO.Breakdown::discipline)
                .thenComparing(DirectedPracticeAnalysisDTO.Breakdown::name)).toList();
    }

    private double percentage(int correct, int total) {
        return total == 0 ? 0 : Math.round(1000.0 * correct / total) / 10.0;
    }
}
