package br.com.provasmart.api.controller;

import br.com.provasmart.api.audit.AuditAccessDeniedHandler;
import br.com.provasmart.api.audit.AuditAuthenticationEntryPoint;
import br.com.provasmart.api.audit.AuditLogInterceptor;
import br.com.provasmart.api.config.SecurityConfig;
import br.com.provasmart.api.dto.request.user.UpdateUserNameRequestDTO;
import br.com.provasmart.api.exception.handler.GlobalExceptionHandler;
import br.com.provasmart.api.service.IUserService;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(UserNameControllerTest.TestConfig.class)
@WebAppConfiguration
@TestPropertySource(properties = "spring.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=")
class UserNameControllerTest {
    @Autowired private WebApplicationContext context;
    @Autowired private IUserService service;
    @Autowired private JwtEncoder jwtEncoder;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        reset(service);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "ESTUDANTE"})
    void authenticatedUsersCanUpdateOwnName(String role) throws Exception {
        mvc.perform(patch("/users/me/name").header("Authorization", "Bearer " + token(role))
                        .contentType("application/json").content("{\"name\":\"Novo nome\"}"))
                .andExpect(status().isOk());
        verify(service).updateCurrentUserName(new UpdateUserNameRequestDTO("Novo nome"));
    }

    @Test
    void rejectsAnonymousRequest() throws Exception {
        mvc.perform(patch("/users/me/name").contentType("application/json").content("{\"name\":\"Nome\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\":null}", "{\"name\":\"\"}", "{\"name\":\"   \"}"})
    void rejectsMissingOrBlankName(String body) throws Exception {
        mvc.perform(patch("/users/me/name").header("Authorization", "Bearer " + token("ESTUDANTE"))
                        .contentType("application/json").content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void accepts150CharactersAndRejects151() throws Exception {
        mvc.perform(patch("/users/me/name").header("Authorization", "Bearer " + token("ESTUDANTE"))
                        .contentType("application/json").content("{\"name\":\"" + "a".repeat(150) + "\"}"))
                .andExpect(status().isOk());
        reset(service);
        mvc.perform(patch("/users/me/name").header("Authorization", "Bearer " + token("ESTUDANTE"))
                        .contentType("application/json").content("{\"name\":\"" + "a".repeat(151) + "\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    private String token(String role) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder().subject(UUID.randomUUID().toString()).claim("role", role)
                .issuedAt(now).expiresAt(now.plusSeconds(300)).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, UserController.class, GlobalExceptionHandler.class})
    static class TestConfig {
        @Bean IUserService userService() { return mock(IUserService.class); }
        @Bean AuditLogInterceptor auditLogInterceptor() { return mock(AuditLogInterceptor.class); }
        @Bean AuditAuthenticationEntryPoint authenticationEntryPoint(AuditLogInterceptor audit) {
            return new AuditAuthenticationEntryPoint(audit);
        }
        @Bean AuditAccessDeniedHandler accessDeniedHandler(AuditLogInterceptor audit) {
            return new AuditAccessDeniedHandler(audit);
        }
    }
}