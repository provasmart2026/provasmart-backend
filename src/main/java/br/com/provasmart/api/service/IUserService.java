package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.user.UserRequestDTO;
import br.com.provasmart.api.dto.request.user.UpdateUserNameRequestDTO;
import br.com.provasmart.api.dto.response.user.UserAdminSummaryResponseDTO;
import br.com.provasmart.api.dto.response.user.UserEmailResponseDTO;
import br.com.provasmart.api.dto.response.user.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {

    UserResponseDTO create(UserRequestDTO userRequestDTO);

    UserAdminSummaryResponseDTO findById(UUID id);

    UserEmailResponseDTO revealEmail(UUID id);

    Page<UserAdminSummaryResponseDTO> findAll(Pageable pageable);

    UserResponseDTO findCurrentUser();

    UserResponseDTO updateCurrentUserName(UpdateUserNameRequestDTO request);

    void activate(UUID id);

    void  deactivate(UUID id);

    void requestDelete();

    void delete(UUID id);
}
