package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.user.UserRequestDTO;
import br.com.provasmart.api.dto.response.user.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {

    UserResponseDTO create(UserRequestDTO userRequestDTO);

    UserResponseDTO findById(UUID id);

    Page<UserResponseDTO> findAll(Pageable pageable);

    void activate(UUID id);

    void  deactivate(UUID id);
}
