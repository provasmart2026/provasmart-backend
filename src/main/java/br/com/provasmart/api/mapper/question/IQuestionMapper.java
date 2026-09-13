    package br.com.provasmart.api.mapper.question;

    import br.com.provasmart.api.domain.entity.questions.QuestionEntity;
    import br.com.provasmart.api.domain.entity.questions.SubjectEntity;
    import br.com.provasmart.api.dto.request.question.QuestionRequestDTO;
    import br.com.provasmart.api.dto.response.question.QuestionResponseDTO;
    import org.mapstruct.AfterMapping;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.MappingTarget;

    @Mapper(componentModel = "spring", uses = {IAlternativeMapper.class})
    public interface IQuestionMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "subject", source = "subjectEntity")
        @Mapping(target = "active", expression = "java(true)")
        @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
        @Mapping(target = "updatedAt", ignore = true)
        QuestionEntity toEntity(QuestionRequestDTO questionRequestDTO, SubjectEntity subjectEntity);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "subject", source = "subjectEntity")
        @Mapping(target = "active", ignore = true)
        @Mapping(target = "alternatives", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
        QuestionEntity toEntityUpdate(QuestionRequestDTO questionRequestDTO, SubjectEntity subjectEntity,
                            @MappingTarget QuestionEntity questionEntity);

        @Mapping(target = "subjectId", source = "subject.id")
        @Mapping(target = "subjectName", source = "subject.name")
        QuestionResponseDTO toDTO(QuestionEntity questionEntity);

        @AfterMapping
        default void linkAlternatives(@MappingTarget QuestionEntity questionEntity) {

            if (questionEntity.getAlternatives() != null) {
                questionEntity.getAlternatives()
                        .forEach(alternative -> alternative.setQuestion(questionEntity));
            }
        }
    }
