package br.com.provasmart.api.controller;

import br.com.provasmart.api.domain.enums.LegalDocumentTypeEnum;
import br.com.provasmart.api.dto.request.legal.LegalAcceptanceRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import br.com.provasmart.api.dto.response.legal.LegalDocumentResponseDTO;
import br.com.provasmart.api.dto.response.legal.LegalAcceptanceStatusResponseDTO;
import br.com.provasmart.api.service.ILegalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/legal-documents")
@RequiredArgsConstructor
public class LegalDocumentController {

    private final ILegalDocumentService legalDocumentService;

    @PostMapping("/acceptance")
    public ResponseEntity<Void> acceptCurrentDocuments(@RequestBody @Valid LegalAcceptanceRequestDTO request) {
        legalDocumentService.acceptCurrentDocuments(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/acceptance-status")
    public ResponseEntity<LegalAcceptanceStatusResponseDTO> findAcceptanceStatus() {
        return ResponseEntity.ok(legalDocumentService.findAcceptanceStatus());
    }

    @GetMapping("/{type}/current")
    public ResponseEntity<LegalDocumentResponseDTO> findCurrent(@PathVariable LegalDocumentTypeEnum type) {
        var document = legalDocumentService.findCurrent(type);
        return ResponseEntity.ok(document);
    }
}
