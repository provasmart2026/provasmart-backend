package br.com.provasmart.api.domain.entity.legal;

import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "legal_documents", uniqueConstraints = {
        @UniqueConstraint(name = "uk_legal_document_type_version", columnNames = {"type", "version"})
})
public class LegalDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30, updatable = false)
    private LegalDocumentTypeEnum type;

    @Column(name = "version", nullable = false, length = 20, updatable = false)
    private String version;

    @Column(name = "title", nullable = false, length = 150, updatable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT", updatable = false)
    private String content;

    @Column(name = "effective_at", nullable = false, updatable = false)
    private LocalDateTime effectiveAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
