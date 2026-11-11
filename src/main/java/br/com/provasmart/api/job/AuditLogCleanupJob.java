package br.com.provasmart.api.job;

import br.com.provasmart.api.repository.audit.IAuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AuditLogCleanupJob {

    private final IAuditLogRepository auditLogRepository;
    private final int retentionDays;

    public AuditLogCleanupJob(IAuditLogRepository auditLogRepository,
                             @Value("${audit.retention-days:90}") int retentionDays) {
        if (retentionDays < 1) {
            throw new IllegalArgumentException("Audit retention must be at least one day.");
        }
        this.auditLogRepository = auditLogRepository;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${audit.cleanup-cron:0 0 3 * * *}", zone = "America/Sao_Paulo")
    @Transactional
    public void cleanup() {
        var cutoff = LocalDateTime.now().minusDays(retentionDays);
        var deleted = auditLogRepository.deleteByOccurredAtBefore(cutoff);
        log.info("Audit cleanup completed: {} expired records deleted.", deleted);
    }
}
