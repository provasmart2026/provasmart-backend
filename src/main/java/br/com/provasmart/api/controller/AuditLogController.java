package br.com.provasmart.api.controller;

import br.com.provasmart.api.dto.response.audit.AuditLogEmailResponseDTO;
import br.com.provasmart.api.dto.response.audit.AuditLogResponseDTO;
import br.com.provasmart.api.service.IAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final IAuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<Page<AuditLogResponseDTO>> findAll(
            @PageableDefault(sort = "occurredAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.findAll(pageable));
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<AuditLogEmailResponseDTO> revealEmail(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(auditLogService.revealEmail(id));
    }
}
