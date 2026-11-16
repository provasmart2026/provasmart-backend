package br.com.provasmart.api.controller;

import br.com.provasmart.api.audit.AuditAccessDeniedHandler;
import br.com.provasmart.api.audit.AuditAuthenticationEntryPoint;
import br.com.provasmart.api.audit.AuditLogInterceptor;
import br.com.provasmart.api.config.SecurityConfig;
import br.com.provasmart.api.domain.enums.QuestionReportOriginEnum;
import br.com.provasmart.api.domain.enums.QuestionReportReasonEnum;
import br.com.provasmart.api.domain.enums.QuestionReportStatusEnum;
import br.com.provasmart.api.dto.request.report.QuestionReportRequestDTO;
import br.com.provasmart.api.dto.request.report.QuestionReportStatusRequestDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportDetailResponseDTO;
import br.com.provasmart.api.dto.response.report.QuestionReportResponseDTO;
import br.com.provasmart.api.exception.BadRequestException;
import br.com.provasmart.api.exception.ConflictException;
import br.com.provasmart.api.exception.NotFoundException;
import br.com.provasmart.api.exception.handler.GlobalExceptionHandler;
import br.com.provasmart.api.service.IQuestionReportService;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(QuestionReportControllerTest.TestConfig.class)
@WebAppConfiguration
@TestPropertySource(properties = "spring.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=")
class QuestionReportControllerTest {
    @Autowired private WebApplicationContext context;
    @Autowired private IQuestionReportService service;
    @Autowired private JwtEncoder jwtEncoder;

    private MockMvc mvc;
    private final UUID activityId = UUID.randomUUID();
    private final UUID questionId = UUID.randomUUID();
    private final String body = """
            {"reason":"OUTRO","description":"Problema no enunciado."}
            """;

    @BeforeEach
    void setup() {
        reset(service);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void studentCanSubmitAndReceivesCreatedReport(boolean practice) throws Exception {
        var origin = practice ? QuestionReportOriginEnum.PRATICA_DIRECIONADA : QuestionReportOriginEnum.SIMULADO;
        var response = new QuestionReportResponseDTO(UUID.randomUUID(), origin, QuestionReportReasonEnum.OUTRO,
                "Problema no enunciado.", QuestionReportStatusEnum.PENDENTE, LocalDateTime.now());
        if (practice) when(service.reportPracticeQuestion(eq(activityId), eq(questionId), any())).thenReturn(response);
        else when(service.reportSimulationQuestion(eq(activityId), eq(questionId), any())).thenReturn(response);

        mvc.perform(post(path(practice)).header("Authorization", "Bearer " + token("ESTUDANTE"))
                        .contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.origin").value(origin.name()))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        var expected = new QuestionReportRequestDTO(QuestionReportReasonEnum.OUTRO, "Problema no enunciado.");
        if (practice) verify(service).reportPracticeQuestion(activityId, questionId, expected);
        else verify(service).reportSimulationQuestion(activityId, questionId, expected);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsUnauthenticatedRequests(boolean practice) throws Exception {
        mvc.perform(post(path(practice)).contentType("application/json").content(body))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsAdministrator(boolean practice) throws Exception {
        mvc.perform(post(path(practice)).header("Authorization", "Bearer " + token("ADMIN"))
                        .contentType("application/json").content(body))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsInvalidToken(boolean practice) throws Exception {
        mvc.perform(post(path(practice)).header("Authorization", "Bearer invalid-token")
                        .contentType("application/json").content(body))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsInvalidInputBeforeCallingService(boolean practice) throws Exception {
        var invalidBodies = List.of(
                "{}",
                "{\"reason\":\"OUTRO\",\"description\":\"   \"}",
                "{\"reason\":null,\"description\":\"Problema\"}",
                "{\"reason\":\"INVALIDO\",\"description\":\"Problema\"}",
                "{\"reason\":\"OUTRO\",\"description\":\"" + "a".repeat(1001) + "\"}");
        for (var invalidBody : invalidBodies) {
            mvc.perform(post(path(practice)).header("Authorization", "Bearer " + token("ESTUDANTE"))
                            .contentType("application/json").content(invalidBody))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void returnsServiceErrorsWithTheirHttpStatus(boolean practice) throws Exception {
        var exceptions = List.of(new ConflictException("Reporte pendente."),
                new NotFoundException("Atividade não encontrada."),
                new BadRequestException("Questão fora da atividade."));
        var statuses = List.of(409, 404, 400);
        for (int index = 0; index < exceptions.size(); index++) {
            if (practice) when(service.reportPracticeQuestion(any(), any(), any())).thenThrow(exceptions.get(index));
            else when(service.reportSimulationQuestion(any(), any(), any())).thenThrow(exceptions.get(index));
            mvc.perform(post(path(practice)).header("Authorization", "Bearer " + token("ESTUDANTE"))
                            .contentType("application/json").content(body))
                    .andExpect(status().is(statuses.get(index)))
                    .andExpect(jsonPath("$.message").value(exceptions.get(index).getMessage()));
            reset(service);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/question-reports", "/question-reports/11111111-1111-4111-8111-111111111111"})
    void adminCanReadReports(String path) throws Exception {
        if (path.equals("/question-reports")) {
            when(service.findReports(any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 10), 0));
        }
        mvc.perform(get(path).header("Authorization", "Bearer " + token("ADMIN")))
                .andExpect(status().isOk());
        if (path.equals("/question-reports")) verify(service).findReports(isNull(), any());
        else verify(service).findReportById(UUID.fromString("11111111-1111-4111-8111-111111111111"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/question-reports", "/question-reports/11111111-1111-4111-8111-111111111111"})
    void studentCannotReadReports(String path) throws Exception {
        mvc.perform(get(path).header("Authorization", "Bearer " + token("ESTUDANTE")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/question-reports", "/question-reports/11111111-1111-4111-8111-111111111111"})
    void anonymousCannotReadReports(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDENTE", "RESOLVIDO", "DESCARTADO"})
    void adminCanFilterAndPaginateReports(String status) throws Exception {
        when(service.findReports(any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 10), 0));
        mvc.perform(get("/question-reports").param("status", status).param("page", "1").param("size", "20")
                        .header("Authorization", "Bearer " + token("ADMIN")))
                .andExpect(status().isOk());
        verify(service).findReports(eq(QuestionReportStatusEnum.valueOf(status)),
                argThat(page -> page.getPageNumber() == 1 && page.getPageSize() == 20));
    }

    @org.junit.jupiter.api.Test
    void rejectsInvalidStatusFilter() throws Exception {
        mvc.perform(get("/question-reports").param("status", "INVALIDO")
                        .header("Authorization", "Bearer " + token("ADMIN")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @org.junit.jupiter.api.Test
    void returnsNotFoundForMissingReport() throws Exception {
        when(service.findReportById(any())).thenThrow(new NotFoundException("Reporte não encontrado."));
        mvc.perform(get("/question-reports/" + UUID.randomUUID()).header("Authorization", "Bearer " + token("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reporte não encontrado."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"RESOLVIDO", "DESCARTADO"})
    void adminCanCloseReport(String target) throws Exception {
        var id = UUID.randomUUID();
        var response = new QuestionReportDetailResponseDTO(id, UUID.randomUUID(), "Estudante",
                UUID.randomUUID(), QuestionReportOriginEnum.SIMULADO, activityId, questionId, null, null,
                QuestionReportReasonEnum.OUTRO, "Descrição", "{}", QuestionReportStatusEnum.valueOf(target),
                LocalDateTime.now().minusDays(1), LocalDateTime.now());
        when(service.updateStatus(eq(id), any())).thenReturn(response);
        mvc.perform(patch("/question-reports/" + id + "/status")
                        .header("Authorization", "Bearer " + token("ADMIN"))
                        .contentType("application/json").content("{\"status\":\"" + target + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(target))
                .andExpect(jsonPath("$.updatedAt").exists());
        verify(service).updateStatus(id, new QuestionReportStatusRequestDTO(QuestionReportStatusEnum.valueOf(target)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ESTUDANTE", "ANONYMOUS"})
    void rejectsStatusChangeWithoutAdminPermission(String role) throws Exception {
        var request = patch("/question-reports/" + UUID.randomUUID() + "/status")
                .contentType("application/json").content("{\"status\":\"RESOLVIDO\"}");
        if (!role.equals("ANONYMOUS")) request.header("Authorization", "Bearer " + token(role));
        mvc.perform(request).andExpect(status().is(role.equals("ANONYMOUS") ? 401 : 403));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"status\":null}", "{\"status\":\"INVALIDO\"}"})
    void validatesStatusRequestBeforeCallingService(String body) throws Exception {
        mvc.perform(patch("/question-reports/" + UUID.randomUUID() + "/status")
                        .header("Authorization", "Bearer " + token("ADMIN"))
                        .contentType("application/json").content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void propagatesStatusTransitionErrors(int code) throws Exception {
        RuntimeException error = switch (code) {
            case 400 -> new BadRequestException("Status inválido.");
            case 404 -> new NotFoundException("Reporte não encontrado.");
            default -> new ConflictException("Reporte encerrado.");
        };
        when(service.updateStatus(any(), any())).thenThrow(error);
        mvc.perform(patch("/question-reports/" + UUID.randomUUID() + "/status")
                        .header("Authorization", "Bearer " + token("ADMIN"))
                        .contentType("application/json").content("{\"status\":\"RESOLVIDO\"}"))
                .andExpect(status().is(code));
    }

    private String path(boolean practice) {
        return (practice ? "/directed-practices/" : "/simulations/")
                + activityId + "/questions/" + questionId + "/reports";
    }

    private String token(String role) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder().subject(UUID.randomUUID().toString())
                .issuedAt(now).expiresAt(now.plusSeconds(300)).claim("role", role).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    @Configuration
    @org.springframework.data.web.config.EnableSpringDataWebSupport
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, QuestionReportController.class, GlobalExceptionHandler.class})
    static class TestConfig {
        @Bean IQuestionReportService questionReportService() { return mock(IQuestionReportService.class); }
        @Bean AuditLogInterceptor auditLogInterceptor() { return mock(AuditLogInterceptor.class); }
        @Bean AuditAuthenticationEntryPoint authenticationEntryPoint(AuditLogInterceptor audit) {
            return new AuditAuthenticationEntryPoint(audit);
        }
        @Bean AuditAccessDeniedHandler accessDeniedHandler(AuditLogInterceptor audit) {
            return new AuditAccessDeniedHandler(audit);
        }
    }
}