CREATE TABLE legal_documents
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type         VARCHAR(30)  NOT NULL,
    version      VARCHAR(20)  NOT NULL,
    title        VARCHAR(150) NOT NULL,
    content      TEXT         NOT NULL,
    effective_at TIMESTAMP    NOT NULL,
    created_at   TIMESTAMP    NOT NULL,

    CONSTRAINT uk_legal_document_type_version UNIQUE (type, version),
    CONSTRAINT ck_legal_document_type CHECK (type IN ('TERMOS_DE_USO', 'POLITICA_DE_PRIVACIDADE'))
);
