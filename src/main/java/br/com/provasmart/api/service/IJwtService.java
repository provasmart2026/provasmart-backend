package br.com.provasmart.api.service;

import br.com.provasmart.api.domain.entity.users.UserEntity;

public interface IJwtService {

    String generateToken(UserEntity userEntity);
}
