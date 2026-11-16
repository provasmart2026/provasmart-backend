package br.com.provasmart.api.mapper.user;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.RoleEnum;
import br.com.provasmart.api.dto.request.user.UserRequestDTO;
import br.com.provasmart.api.dto.response.user.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(RoleEnum.ESTUDANTE)")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "deletionRequested", constant = "false")
    @Mapping(target = "termsAcceptedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "termsVersion", source = "termsVersion")
    @Mapping(target = "privacyAcceptedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "privacyVersion", source = "privacyVersion")
    @Mapping(target = "ageDeclarationAcceptedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletionRequestedAt", ignore = true)
    @Mapping(target = "authenticationCodes", ignore = true)
    @Mapping(target = "simulations", ignore = true)
    UserEntity toEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toResponseDTO(UserEntity userEntity);
}
