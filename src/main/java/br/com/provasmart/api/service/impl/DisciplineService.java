package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.questions.DisciplineEntity;
import br.com.provasmart.api.dto.response.question.DisciplineResponseDTO;
import br.com.provasmart.api.mapper.question.IDisciplineMapper;
import br.com.provasmart.api.repository.questions.IDisciplineRepository;
import br.com.provasmart.api.service.IDisciplineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class DisciplineService implements IDisciplineService {

    private final IDisciplineRepository disciplineRepository;
    private final IDisciplineMapper disciplineMapper;

    @Override
    public List<DisciplineResponseDTO> findAllByExamArea(UUID examAreaId) {
        log.info("Finding all disciplines by exam area id: {}", examAreaId);
        var disciplines = getAllByExamArea(examAreaId);
        return getList(disciplines);
    }

    @Override
    public DisciplineEntity findById(UUID id) {
        log.info("Finding discipline by id: {}", id);
        return getDisciplineEntity(id);
    }

    private DisciplineEntity getDisciplineEntity(UUID id) {
        log.info("Finding discipline by id: {}", id);
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discipline not found with id: " + id));
    }

    private List<DisciplineResponseDTO> getList(List<DisciplineEntity> disciplines) {
        return disciplines.stream()
                .map(disciplineMapper::toResponseDTO)
                .toList();
    }

    private List<DisciplineEntity> getAllByExamArea(UUID examAreaId) {
        return disciplineRepository
                .findAllByExamArea_IdOrderByNameAsc(examAreaId);
    }

}
