package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.practice.DirectedPracticeAlternativeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeEntity;
import br.com.provasmart.api.domain.entity.practice.DirectedPracticeQuestionEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.enums.DirectedPracticeStatus;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.practice.DirectedPracticeAnalysisDTO;
import br.com.provasmart.api.dto.practice.DirectedPracticeDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.repository.practice.IDirectedPracticeRepository;
import br.com.provasmart.api.repository.questions.IQuestionRepository;
import br.com.provasmart.api.repository.simulations.ISimulationRepository;
import br.com.provasmart.api.repository.users.IUserRepository;
import br.com.provasmart.api.service.ICurrentActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
public class DirectedPracticeService {
    private static final int QUESTION_COUNT = 20;

    private final IDirectedPracticeRepository practiceRepository;
    private final ISimulationRepository simulationRepository;
    private final IQuestionRepository questionRepository;
    private final IUserRepository userRepository;
    private final ICurrentActorService currentActorService;

    @Transactional
    public DirectedPracticeDTO create() {
        UUID studentId = currentActorService.getCurrentUserId();
        var student = userRepository.findByIdForUpdate(studentId)
                .orElseThrow(() -> new NotFoundException("Estudante não encontrado."));
        if (practiceRepository.existsByStudent_IdAndStatus(studentId, DirectedPracticeStatus.EM_ANDAMENTO)) {
            throw new ConflictException("Já existe uma prática direcionada em andamento.");
        }
        SimulationEntity simulation = simulationRepository
                .findFirstByStudent_IdAndStatusOrderByFinishedAtDesc(studentId, SimulationStatusEnum.FINALIZADO)
                .orElseThrow(() -> new ConflictException("Finalize um Simulado Geral antes de iniciar a prática direcionada."));
        ExamAreaEnum area = selectWeakestArea(simulation, studentId);
        List<QuestionEntity> pool = questionRepository.findAllByActiveTrueAndSubjectDisciplineExamArea(area);
        Map<UUID, QuestionEntity> distinct = pool.stream()
                .filter(question -> question.getAlternatives().stream().filter(alternative -> alternative.isCorrect()).count() == 1)
                .collect(Collectors.toMap(QuestionEntity::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        if (distinct.size() < QUESTION_COUNT) {
            throw new ConflictException("Não há 20 questões ativas disponíveis para a área selecionada.");
        }
        List<QuestionEntity> shuffled = new ArrayList<>(distinct.values());
        Collections.shuffle(shuffled);

        var practice = new DirectedPracticeEntity();
        practice.setStudent(student);
        practice.setSourceSimulation(simulation);
        practice.setArea(area);
        practice.setStatus(DirectedPracticeStatus.EM_ANDAMENTO);
        practice.setStartedAt(LocalDateTime.now());
        for (int i = 0; i < QUESTION_COUNT; i++) {
            practice.getQuestions().add(snapshot(practice, shuffled.get(i), i + 1));
        }
        return toDTO(practiceRepository.saveAndFlush(practice));
    }

    @Transactional(readOnly = true)
    public List<DirectedPracticeDTO> list() {
        return practiceRepository.findByStudent_IdOrderByStartedAtDesc(currentActorService.getCurrentUserId())
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public DirectedPracticeDTO findById(UUID id) {
        return toDTO(owned(id));
    }

    @Transactional
    public DirectedPracticeDTO answer(UUID id, UUID questionId, UUID alternativeId) {
        DirectedPracticeEntity practice = owned(id);
        requireInProgress(practice);
        DirectedPracticeQuestionEntity question = practice.getQuestions().stream()
                .filter(item -> item.getId().equals(questionId)).findFirst()
                .orElseThrow(() -> new BadRequestException("A questão não pertence a esta prática."));
        boolean belongs = question.getAlternatives().stream().anyMatch(item -> item.getId().equals(alternativeId));
        if (!belongs) throw new BadRequestException("A alternativa não pertence a esta questão.");
        question.setSelectedAlternativeId(alternativeId);
        question.setAnsweredAt(LocalDateTime.now());
        return toDTO(practiceRepository.save(practice));
    }

    @Transactional
    public DirectedPracticeDTO finish(UUID id) {
        DirectedPracticeEntity practice = owned(id);
        requireInProgress(practice);
        if (practice.getQuestions().size() != QUESTION_COUNT || practice.getQuestions().stream()
                .anyMatch(item -> item.getSelectedAlternativeId() == null)) {
            throw new BadRequestException("Responda as 20 questões antes de finalizar a prática.");
        }
        practice.getQuestions().forEach(item -> item.setCorrectAtFinish(isCorrect(item)));
        practice.setStatus(DirectedPracticeStatus.FINALIZADA);
        practice.setFinishedAt(LocalDateTime.now());
        return toDTO(practiceRepository.save(practice));
    }

    @Transactional
    public DirectedPracticeDTO cancel(UUID id) {
        DirectedPracticeEntity practice = owned(id);
        requireInProgress(practice);
        practice.setStatus(DirectedPracticeStatus.CANCELADA);
        practice.setFinishedAt(LocalDateTime.now());
        return toDTO(practiceRepository.save(practice));
    }

    @Transactional(readOnly = true)
    public DirectedPracticeAnalysisDTO analysis(UUID id) {
        DirectedPracticeEntity practice = owned(id);
        if (practice.getStatus() != DirectedPracticeStatus.FINALIZADA) {
            throw new ConflictException("A correção só fica disponível após a finalização da prática.");
        }
        List<DirectedPracticeQuestionEntity> questions = practice.getQuestions();
        int correct = (int) questions.stream().filter(item -> Boolean.TRUE.equals(item.getCorrectAtFinish())).count();
        var disciplines = breakdown(questions, DirectedPracticeQuestionEntity::getDisciplineName);
        var subjects = subjectBreakdown(questions);
        var review = questions.stream().map(question -> {
            var right = question.getAlternatives().stream().filter(DirectedPracticeAlternativeEntity::isCorrect)
                    .findFirst().orElseThrow();
            var selected = question.getAlternatives().stream()
                    .filter(item -> item.getId().equals(question.getSelectedAlternativeId())).findFirst().orElseThrow();
            return new DirectedPracticeAnalysisDTO.Review(question.getId(), question.getPosition(),
                    question.getStatement(), question.getExplanation(), question.getDisciplineName(),
                    question.getSubjectName(), selected.getId(), right.getId(), selected.getLetter(),
                    right.getLetter(), Boolean.TRUE.equals(question.getCorrectAtFinish()),
                    question.getAlternatives().stream().map(this::toAlternative).toList());
        }).toList();
        return new DirectedPracticeAnalysisDTO(practice.getId(), practice.getArea(), questions.size(), correct,
                percentage(correct, questions.size()), disciplines, subjects, review);
    }

    private ExamAreaEnum selectWeakestArea(SimulationEntity simulation, UUID studentId) {
        return Arrays.stream(ExamAreaEnum.values()).min(Comparator
                .comparingDouble((ExamAreaEnum area) -> simulation.getQuestions().stream()
                        .filter(question -> question.getQuestion().getSubject().getDiscipline().getExamArea() == area)
                        .mapToInt(question -> question.getAnswer() != null
                                && question.getAnswer().getSelectedAlternative().isCorrect() ? 1 : 0)
                        .average().orElse(1.0))
                .thenComparingLong(area -> practiceRepository.countByStudent_IdAndArea(studentId, area))
                .thenComparing(area -> practiceRepository.findFirstByStudent_IdAndAreaOrderByStartedAtDesc(studentId, area)
                        .map(DirectedPracticeEntity::getStartedAt).orElse(LocalDateTime.MIN))
                .thenComparingInt(Enum::ordinal)).orElseThrow();
    }

    private DirectedPracticeQuestionEntity snapshot(DirectedPracticeEntity practice, QuestionEntity original, int position) {
        if (original.getAlternatives().stream().filter(alternative -> alternative.isCorrect()).count() != 1) {
            throw new ConflictException("Uma questão ativa da área selecionada não possui gabarito válido.");
        }
        var question = new DirectedPracticeQuestionEntity();
        question.setPractice(practice);
        question.setOriginalQuestionId(original.getId());
        question.setPosition(position);
        question.setStatement(original.getStatement());
        question.setExplanation(original.getExplanation());
        question.setDisciplineName(original.getSubject().getDiscipline().getName());
        question.setSubjectName(original.getSubject().getName());
        var shuffledAlternatives = new ArrayList<>(original.getAlternatives());
        Collections.shuffle(shuffledAlternatives);
        for (int i = 0; i < shuffledAlternatives.size(); i++) {
            var originalAlternative = shuffledAlternatives.get(i);
            var alternative = new DirectedPracticeAlternativeEntity();
            alternative.setPracticeQuestion(question);
            alternative.setOriginalAlternativeId(originalAlternative.getId());
            alternative.setLetter(String.valueOf((char) ('A' + i)));
            alternative.setText(originalAlternative.getText());
            alternative.setCorrect(originalAlternative.isCorrect());
            question.getAlternatives().add(alternative);
        }
        return question;
    }

    private DirectedPracticeEntity owned(UUID id) {
        return practiceRepository.findByIdAndStudent_Id(id, currentActorService.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Prática direcionada não encontrada."));
    }

    private void requireInProgress(DirectedPracticeEntity practice) {
        if (practice.getStatus() != DirectedPracticeStatus.EM_ANDAMENTO) {
            throw new ConflictException("A prática não está em andamento.");
        }
    }

    private DirectedPracticeDTO toDTO(DirectedPracticeEntity practice) {
        return new DirectedPracticeDTO(practice.getId(),
                practice.getSourceSimulation() == null ? null : practice.getSourceSimulation().getId(),
                practice.getArea(), practice.getStatus(), practice.getStartedAt(), practice.getFinishedAt(),
                practice.getQuestions().stream().map(question -> new DirectedPracticeDTO.Question(
                        question.getId(), question.getPosition(), question.getStatement(),
                        question.getDisciplineName(), question.getSubjectName(),
                        question.getAlternatives().stream().map(this::toAlternative).toList(),
                        question.getSelectedAlternativeId())).toList());
    }

    private DirectedPracticeDTO.Alternative toAlternative(DirectedPracticeAlternativeEntity item) {
        return new DirectedPracticeDTO.Alternative(item.getId(), item.getLetter(), item.getText());
    }

    private boolean isCorrect(DirectedPracticeQuestionEntity question) {
        return question.getAlternatives().stream().anyMatch(item -> item.isCorrect()
                && item.getId().equals(question.getSelectedAlternativeId()));
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
