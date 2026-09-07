package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.ExamAreaEntity;
import br.com.provasmart.api.dto.response.question.ExamAreaResponseDTO;
import br.com.provasmart.api.mapper.question.IExamAreaMapper;
import br.com.provasmart.api.repository.questions.IExamAreaRepository;
import br.com.provasmart.api.service.IExamAreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExamAreaService implements IExamAreaService {

    private final IExamAreaRepository examAreaRepository;
    private final IExamAreaMapper examAreaMapper;

    @Override
    public List<ExamAreaResponseDTO> findAll() {
        log.info("Finding all exam areas");
        var examAreas = getAllByOrderByNameAsc();
        return getList(examAreas);
    }

    private List<ExamAreaResponseDTO> getList(List<ExamAreaEntity> examAreas) {
        log.info("Mapping exam areas to response DTOs");
        return examAreas.stream()
                .map(examAreaMapper::toResponseDTO)
                .toList();
    }

    private List<ExamAreaEntity> getAllByOrderByNameAsc() {
        log.info("Finding all exam areas ordered by name");
        return examAreaRepository.findAllByOrderByNameAsc();
    }


}
