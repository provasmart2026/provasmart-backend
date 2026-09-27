package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.AlternativeEntity;
import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationAnswerEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationEntity;
import br.com.provasmart.api.domain.entity.simulations.SimulationQuestionEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.ExamAreaEnum;
import br.com.provasmart.api.domain.enums.SimulationStatusEnum;
import br.com.provasmart.api.dto.request.simulation.SimulationAnswerRequestDTO;
import br.com.provasmart.api.dto.response.simulation.SimulationResponseDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
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
import br.com.provasmart.api.service.ISimulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class SimulationService implements ISimulationService {

    private static final int QUESTIONS_PER_AREA = 10;

    private final ISimulationRepository simulationRepository;

    private final ISimulationQuestionRepository simulationQuestionRepository;

    private final ISimulationAnswerRepository simulationAnswerRepository;

    private final IQuestionRepository questionRepository;

    private final IAlternativeRepository alternativeRepository;

    private final ISimulationMapper simulationMapper;

    private final ISimulationQuestionMapper simulationQuestionMapper;

    private final ISimulationAnswerMapper simulationAnswerMapper;

    private final IUserRepository userRepository;

    private final ICurrentActorService currentActorService;

    @Override
    public SimulationResponseDTO create() {
        var studentId = getCurrentStudentId();

        log.info("Creating a new simulation for student with ID {}", studentId);

        var student = findStudentById(studentId);

        validateStudentHasNoSimulationInProgress(studentId);

        var simulationEntity = simulationEntity(student);

        addQuestionsToSimulation(simulationEntity);

        var savedSimulation = save(simulationEntity);

        return mapToDto(savedSimulation);
    }


    @Override
    public SimulationResponseDTO findById(UUID simulationId) {
        var studentId = getCurrentStudentId();

        log.info("Finding simulation for ID {}", simulationId);

        var simulationEntity = getSimulationById(simulationId, studentId);

        return mapToDto(simulationEntity);
    }

    @Override
    public SimulationResponseDTO answerQuestion(UUID simulationId, UUID simulationQuestionId, SimulationAnswerRequestDTO answerRequestDTO) {
        var studentId = getCurrentStudentId();

        log.info("Answering question with Id {} for simulation with ID {}", simulationQuestionId, simulationId);

        var simulationEntity = getSimulationById(simulationId, studentId);

        validateSimulationIsInProgress(simulationEntity);

        var simulationQuestion = getSimulationQuestionById(simulationQuestionId);

        validateQuestionBelongsToSimulation(simulationEntity, simulationQuestion);

        var alternative = getAlternativeById(answerRequestDTO.alternativeId());

        validateAlternativeBelongsToQuestion(simulationQuestion, alternative);

        var answerEntity = getOrCreateAnswer(simulationQuestionId);

        simulationAnswerMapper.toEntity(simulationQuestion, alternative, answerEntity);

        var savedAnswer = saveAnswer(answerEntity);

        simulationQuestion.setAnswer(savedAnswer);

        return mapToDto(simulationEntity);

    }

    @Override
    public SimulationResponseDTO finishSimulation(UUID simulationId) {
        var studentId = getCurrentStudentId();

        log.info("Finishing simulation for ID {}", simulationId);

        var simulationEntity = getSimulationById(simulationId, studentId);

        validateSimulationIsInProgress(simulationEntity);

        validateAllQuestionsAnswered(simulationEntity);

        finishSimulationEntity(simulationEntity);

        var savedSimulation = save(simulationEntity);

        return mapToDto(savedSimulation);
    }

    private UUID getCurrentStudentId() {
        log.info("Getting current student ID");
        return currentActorService.getCurrentUserId();
    }

    private static void finishSimulationEntity(SimulationEntity simulationEntity) {
        log.info("Setting simulation status to FINALIZADO");

        simulationEntity.setStatus(SimulationStatusEnum.FINALIZADO);
        simulationEntity.setFinishedAt(LocalDateTime.now());
    }

    private void validateAllQuestionsAnswered(SimulationEntity simulationEntity) {
        log.info("Validating if all questions have been answered for simulation with ID {}", simulationEntity.getId());

        var hasUnansweredQuestion = simulationEntity.getQuestions().stream()
                .anyMatch(question -> question.getAnswer() == null);

        if (hasUnansweredQuestion) {
            log.error("Not all questions have been answered for simulation with ID {}", simulationEntity.getId());
            throw new BadRequestException("Todas as questões devem ser respondidas antes de finalizar o simulado.");
        }
    }

    private SimulationAnswerEntity saveAnswer(SimulationAnswerEntity answerEntity) {
        log.info("Saving simulation answer");

        return simulationAnswerRepository.save(answerEntity);

    }

    private SimulationEntity save(SimulationEntity simulationEntity) {
        log.info("Saving simulation with ID {}", simulationEntity.getId());
        return simulationRepository.save(simulationEntity);
    }

    private SimulationResponseDTO mapToDto(SimulationEntity simulationEntity) {
        log.info("Mapping simulationEntity to DTO {}", simulationEntity);
        return simulationMapper.toDTO(simulationEntity);
    }

    private void validateStudentHasNoSimulationInProgress(UUID studentId) {
        log.info("Validating if student with ID {} has a simulation in progress", studentId);

        var hasSimulationInProgress =
                simulationRepository.existsByStudent_IdAndStatus(studentId, SimulationStatusEnum.EM_ANDAMENTO);

        if (hasSimulationInProgress) {
            log.error("Student with ID {} already has a simulation in progress", studentId);
            throw new ConflictException("Já existe um simulado em andamento para este estudante.");
        }
    }

    private SimulationEntity simulationEntity(UserEntity student) {
        log.info("Mapping student ID {} to SimulationEntity", student.getId());
        return simulationMapper.toEntity(student);
    }

    private List<QuestionEntity> selectQuestionsByArea(ExamAreaEnum examArea) {
        log.info("Selecting questions for exam area {}", examArea);

        var questions = questionRepository.findAllByActiveTrueAndSubjectDisciplineExamArea(examArea);

        if (questions.size() < QUESTIONS_PER_AREA) {
            log.error("Insufficient active questions for exam area {}: required {}, found {}",
                    examArea, QUESTIONS_PER_AREA, questions.size());
            throw new ConflictException("Não há questões suficientes para a área de prova: " + examArea);
        }

        var randomQuestions = new ArrayList<>(questions);

        Collections.shuffle(randomQuestions);

        return randomQuestions.stream().limit(QUESTIONS_PER_AREA).toList();
    }

    private void addQuestionsToSimulation(SimulationEntity simulationEntity) {
        log.info("Adding questions to simulation for student with ID {}", simulationEntity.getStudent().getId());
        var position = 1;

        for (var examArea : ExamAreaEnum.values()) {

            var selectedQuestions = selectQuestionsByArea(examArea);

            for (var question : selectedQuestions) {

                var simulationQuestion = simulationQuestionMapper.toEntity(simulationEntity, question, position++);

                simulationEntity.getQuestions().add(simulationQuestion);
            }
        }
    }

    private SimulationEntity getSimulationById(UUID simulationId, UUID studentId) {
        log.info("Getting simulation with ID {} for student with ID {}", simulationId, studentId);

        return simulationRepository.findByIdAndStudent_Id(simulationId, studentId)
                .orElseThrow(() -> {
                    log.error("Simulation with ID {} not found", simulationId);
                    return new NotFoundException("Simulado não encontrado.");
                });
    }

    private void validateSimulationIsInProgress(SimulationEntity simulationEntity) {
        log.info("Validating if simulation with ID {} is in progress", simulationEntity.getId());

        if (simulationEntity.getStatus() != SimulationStatusEnum.EM_ANDAMENTO) {
            log.error("Simulation with ID {} is not in progress: current status {}",
                    simulationEntity.getId(), simulationEntity.getStatus());
            throw new ConflictException("O simulado não está em andamento.");
        }
    }

    private SimulationQuestionEntity getSimulationQuestionById(UUID simulationQuestionId) {
        log.info("Getting simulation question with ID {}", simulationQuestionId);

        return simulationQuestionRepository.findById(simulationQuestionId)
                .orElseThrow(() -> {
                    log.error("Simulation question with ID {} not found", simulationQuestionId);
                    return new NotFoundException("Questão do simulado não encontrada.");
                });
    }

    private AlternativeEntity getAlternativeById(UUID alternativeId) {
        log.info("Getting alternative with ID {}", alternativeId);

        return alternativeRepository.findById(alternativeId)
                .orElseThrow(() -> {
                    log.error("Alternative with ID {} not found", alternativeId);
                    return new NotFoundException("Alternativa não encontrada.");
                });
    }

    private void validateQuestionBelongsToSimulation(SimulationEntity simulationEntity, SimulationQuestionEntity simulationQuestion) {
        log.info("Validating if simulation question with ID {} belongs to simulation with ID {}", simulationQuestion.getId(), simulationEntity.getId());

        if (!simulationQuestion.getSimulation().getId().equals(simulationEntity.getId())) {
            log.error("Simulation question with ID {} belongs to simulation with ID {}, not requested simulation with ID {}",
                    simulationQuestion.getId(), simulationQuestion.getSimulation().getId(), simulationEntity.getId());
            throw new BadRequestException("A questão não pertence a este simulado.");
        }
    }

    private void validateAlternativeBelongsToQuestion(SimulationQuestionEntity simulationQuestion, AlternativeEntity alternative) {
        log.info("Validating if alternative with ID {} belongs to question with ID {}", alternative.getId(), simulationQuestion.getQuestion().getId());

        if (!alternative.getQuestion().getId().equals(simulationQuestion.getQuestion().getId())) {
            log.error("Alternative with ID {} belongs to question with ID {}, not question with ID {} for simulation question with ID {}",
                    alternative.getId(), alternative.getQuestion().getId(),
                    simulationQuestion.getQuestion().getId(), simulationQuestion.getId());
            throw new BadRequestException("A alternativa não pertence à questão do simulado.");
        }
    }

    private SimulationAnswerEntity getOrCreateAnswer(UUID simulationQuestionId) {
        log.info("Getting or creating answer for question with ID {}", simulationQuestionId);
        return simulationAnswerRepository.findBySimulationQuestionId(simulationQuestionId)
                .orElseGet(SimulationAnswerEntity::new);
    }

    private UserEntity findStudentById(UUID studentId) {
        log.info("Finding student with ID {}", studentId);

        return userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Student with ID {} not found", studentId);
                    return new NotFoundException("Estudante não encontrado.");
                });
    }

}
