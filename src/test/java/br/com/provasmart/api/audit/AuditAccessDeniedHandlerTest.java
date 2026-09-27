package br.com.provasmart.api.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditAccessDeniedHandlerTest {

    @Mock
    private AuditLogInterceptor auditLogInterceptor;

    @InjectMocks
    private AuditAccessDeniedHandler accessDeniedHandler;

    @Test
    void shouldRecordForbiddenAccess() throws Exception {
        var request = new MockHttpServletRequest("GET", "/audit-logs");
        var response = new MockHttpServletResponse();
        var exception = mock(AccessDeniedException.class);

        accessDeniedHandler.handle(request, response, exception);

        verify(auditLogInterceptor).recordDenied(request, 403);
        assertEquals(403, response.getStatus());
    }
}
