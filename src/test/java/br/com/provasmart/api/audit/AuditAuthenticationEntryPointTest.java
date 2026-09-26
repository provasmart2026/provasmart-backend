package br.com.provasmart.api.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditAuthenticationEntryPointTest {

    @Mock
    private AuditLogInterceptor auditLogInterceptor;

    @InjectMocks
    private AuditAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    void shouldRecordUnauthorizedAccess() throws Exception {
        var request = new MockHttpServletRequest("GET", "/users");
        var response = new MockHttpServletResponse();
        var exception = mock(AuthenticationException.class);

        authenticationEntryPoint.commence(request, response, exception);

        verify(auditLogInterceptor).recordDenied(request, 401);
        assertEquals(401, response.getStatus());
    }
}
