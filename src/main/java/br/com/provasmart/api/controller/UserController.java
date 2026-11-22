package br.com.provasmart.api.controller;

import br.com.provasmart.api.audit.AuditContext;
import br.com.provasmart.api.dto.request.user.UserRequestDTO;
import br.com.provasmart.api.dto.request.user.UpdateUserNameRequestDTO;
import br.com.provasmart.api.dto.response.user.UserAdminSummaryResponseDTO;
import br.com.provasmart.api.dto.response.user.UserEmailResponseDTO;
import br.com.provasmart.api.dto.response.user.UserResponseDTO;
import br.com.provasmart.api.service.IUserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO requestDTO,
                                                  HttpServletRequest request) {
        AuditContext.setActorEmail(request, requestDTO.email());
        var user = userService.create(requestDTO);
        var location = URI.create("/users/" + user.id());
        return ResponseEntity.created(location).body(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> findCurrentUser() {
        var user = userService.findCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me/name")
    public ResponseEntity<UserResponseDTO> updateCurrentUserName(
            @RequestBody @Valid UpdateUserNameRequestDTO request) {
        return ResponseEntity.ok(userService.updateCurrentUserName(request));
    }

    @PatchMapping("/me/request-deletion")
    public ResponseEntity<Void> requestDelete() {
        userService.requestDelete();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAdminSummaryResponseDTO> findById(@PathVariable UUID id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<UserEmailResponseDTO> revealEmail(@PathVariable UUID id) {
        var email = userService.revealEmail(id);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(email);
    }

    @GetMapping
    public ResponseEntity<Page<UserAdminSummaryResponseDTO>> findAll(Pageable pageable) {
        var users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        userService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
