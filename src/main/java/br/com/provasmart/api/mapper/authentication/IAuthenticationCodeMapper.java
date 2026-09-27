package br.com.provasmart.api.mapper.authentication;

import br.com.provasmart.api.domain.entity.authentication.AuthenticationCodeEntity;
import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.domain.enums.AuthenticationCodePurposeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface IAuthenticationCodeMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "used", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    AuthenticationCodeEntity toEntity(UserEntity user, String code, AuthenticationCodePurposeEnum purpose, LocalDateTime expiresAt);
}
