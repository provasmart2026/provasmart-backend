package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.audit.AuditLogEntity;
import br.com.provasmart.api.repository.audit.IAuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private IAuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void shouldRecordSuccessfulOperation() {
        var actorId = UUID.randomUUID();

        auditLogService.record(
                actorId,
                "admin@provasmart.com",
                "ACTIVATE",
                "USERS",
                UUID.randomUUID(),
                "PATCH",
                "/users/1/activate",
                204
        );

        var captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(auditLogRepository).save(captor.capture());

        var auditLog = captor.getValue();
        assertEquals(actorId, auditLog.getActorId());
        assertEquals("ACTIVATE", auditLog.getAction());
        assertEquals(204, auditLog.getStatusCode());
        assertTrue(auditLog.isSuccess());
        assertNotNull(auditLog.getOccurredAt());
    }

    @Test
    void shouldRecordFailedOperation() {
        auditLogService.record(
                null,
                null,
                "LOGIN",
                "AUTH",
                null,
                "POST",
                "/auth/login",
                401
        );

        var captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(auditLogRepository).save(captor.capture());

        assertFalse(captor.getValue().isSuccess());
    }
}
