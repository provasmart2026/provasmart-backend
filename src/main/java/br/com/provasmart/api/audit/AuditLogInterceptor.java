package br.com.provasmart.api.audit;

import br.com.provasmart.api.service.IAuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogInterceptor implements HandlerInterceptor {

    private static final Set<String> AUDITED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}"
    );

    private final IAuditLogService auditLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        if (!AUDITED_METHODS.contains(request.getMethod())) {
            return;
        }

        record(request, response.getStatus(), response.getHeader("Location"));
    }

    public void recordDenied(HttpServletRequest request, int statusCode) {
        if (!request.getMethod().equals("OPTIONS") && !request.getRequestURI().equals("/error")) {
            record(request, statusCode, null);
        }
    }

    private void record(HttpServletRequest request, int statusCode, String location) {
        try {
            var jwt = currentJwt();
            var actorId = actorId(jwt);
            var actorEmail = jwt == null ? AuditContext.getActorEmail(request) : jwt.getClaimAsString("email");
            var endpoint = request.getRequestURI();

            auditLogService.record(
                    actorId,
                    actorEmail,
                    action(request.getMethod(), endpoint),
                    resource(endpoint),
                    resourceId(request.getMethod(), endpoint, location),
                    request.getMethod(),
                    endpoint,
                    statusCode
            );
        } catch (RuntimeException auditException) {
            log.error("Could not record audit log for {} {}", request.getMethod(), request.getRequestURI(),
                    auditException);
        }
    }

    private Jwt currentJwt() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        return null;
    }

    private UUID actorId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return null;
        }

        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private String action(String method, String endpoint) {
        if (endpoint.equals("/auth/login")) {
            return "LOGIN";
        }
        if (endpoint.equals("/auth/verify-2fa")) {
            return "VERIFY_2FA";
        }
        if (endpoint.equals("/auth/forgot-password")) {
            return "REQUEST_PASSWORD_RESET";
        }
        if (endpoint.equals("/auth/reset-password")) {
            return "RESET_PASSWORD";
        }
        if (endpoint.endsWith("/request-deletion")) {
            return "REQUEST_DELETION";
        }
        if (endpoint.endsWith("/activate")) {
            return "ACTIVATE";
        }
        if (endpoint.endsWith("/deactivate")) {
            return "DEACTIVATE";
        }
        if (endpoint.endsWith("/answer")) {
            return "ANSWER_QUESTION";
        }
        if (endpoint.endsWith("/finish")) {
            return "FINISH_SIMULATION";
        }

        return switch (method) {
            case "GET" -> "READ";
            case "POST" -> "CREATE";
            case "DELETE" -> "DELETE";
            default -> "UPDATE";
        };
    }

    private String resource(String endpoint) {
        return Arrays.stream(endpoint.split("/"))
                .filter(segment -> !segment.isBlank())
                .findFirst()
                .map(segment -> segment.toUpperCase(Locale.ROOT))
                .orElse("UNKNOWN");
    }

    private UUID resourceId(String method, String endpoint, String location) {
        if (method.equals("POST")) {
            var createdResourceId = firstUuid(location);

            if (createdResourceId != null) {
                return createdResourceId;
            }
        }

        return firstUuid(endpoint);
    }

    private UUID firstUuid(String value) {
        if (value == null) {
            return null;
        }

        var matcher = UUID_PATTERN.matcher(value);
        return matcher.find() ? UUID.fromString(matcher.group()) : null;
    }
}
