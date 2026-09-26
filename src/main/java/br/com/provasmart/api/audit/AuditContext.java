package br.com.provasmart.api.audit;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

public final class AuditContext {

    private static final String ACTOR_EMAIL = AuditContext.class.getName() + ".actorEmail";

    private AuditContext() {
    }

    public static void setActorEmail(HttpServletRequest request, String email) {
        if (email != null && !email.isBlank()) {
            request.setAttribute(ACTOR_EMAIL, email.trim().toLowerCase(Locale.ROOT));
        }
    }

    public static String getActorEmail(HttpServletRequest request) {
        var email = request.getAttribute(ACTOR_EMAIL);
        return email instanceof String value ? value : null;
    }
}
