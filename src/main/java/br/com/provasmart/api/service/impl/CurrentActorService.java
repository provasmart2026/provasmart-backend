package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.service.ICurrentActorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CurrentActorService implements ICurrentActorService {

    public UUID getCurrentUserId() {
        var authentication = currentAuthentication();
        var subject = jwt(authentication).getSubject();

        if (subject == null || subject.isBlank()) {
            throw new BadCredentialsException("Identificação do usuário autenticado inválida.");
        }

        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new BadCredentialsException("Identificação do usuário autenticado inválida.", exception);
        }
    }

    private Authentication currentAuthentication() {
        log.info("Retrieving current authentication from security context.");
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado.");
        }

        return authentication;
    }

    private Jwt jwt(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado com JWT.");
    }
}
