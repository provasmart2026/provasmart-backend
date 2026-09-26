package br.com.provasmart.api.audit;

import br.com.provasmart.api.service.IAuditLogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuditLogInterceptorTest {

    @Mock
    private IAuditLogService auditLogService;

    @InjectMocks
    private AuditLogInterceptor auditLogInterceptor;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRecordAuthenticatedOperation() {
        var actorId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        var jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(actorId.toString())
                .claim("email", "admin@provasmart.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        var request = new MockHttpServletRequest("PATCH", "/questions/" + resourceId + "/deactivate");
        var response = new MockHttpServletResponse();
        response.setStatus(204);

        auditLogInterceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).record(
                actorId,
                "admin@provasmart.com",
                "DEACTIVATE",
                "QUESTIONS",
                resourceId,
                "PATCH",
                "/questions/" + resourceId + "/deactivate",
                204
        );
    }

    @Test
    void shouldRecordPublicLoginWithoutRequestData() {
        var request = new MockHttpServletRequest("POST", "/auth/login");
        var response = new MockHttpServletResponse();
        response.setStatus(401);
        AuditContext.setActorEmail(request, " Estudante@ProvaSmart.com ");

        auditLogInterceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).record(
                isNull(),
                eq("estudante@provasmart.com"),
                eq("LOGIN"),
                eq("AUTH"),
                isNull(),
                eq("POST"),
                eq("/auth/login"),
                eq(401)
        );
    }

    @Test
    void shouldRecordAccessDeniedForAuthenticatedUser() {
        var actorId = UUID.randomUUID();
        var jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(actorId.toString())
                .claim("email", "student@provasmart.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        var request = new MockHttpServletRequest("GET", "/audit-logs");

        auditLogInterceptor.recordDenied(request, 403);

        verify(auditLogService).record(
                actorId,
                "student@provasmart.com",
                "READ",
                "AUDIT-LOGS",
                null,
                "GET",
                "/audit-logs",
                403
        );
    }

    @Test
    void shouldIdentifyCreatedResourceFromLocation() {
        var resourceId = UUID.randomUUID();
        var request = new MockHttpServletRequest("POST", "/questions");
        var response = new MockHttpServletResponse();
        response.setStatus(201);
        response.setHeader("Location", "/questions/" + resourceId);

        auditLogInterceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).record(
                isNull(),
                isNull(),
                eq("CREATE"),
                eq("QUESTIONS"),
                eq(resourceId),
                eq("POST"),
                eq("/questions"),
                eq(201)
        );
    }

    @Test
    void shouldIgnoreReadOnlyOperation() {
        var request = new MockHttpServletRequest("GET", "/questions");
        var response = new MockHttpServletResponse();

        auditLogInterceptor.afterCompletion(request, response, new Object(), null);

        verifyNoInteractions(auditLogService);
    }
}
