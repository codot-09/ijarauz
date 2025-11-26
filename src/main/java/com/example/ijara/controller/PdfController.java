package com.example.ijara.controller;

import com.example.ijara.entity.Contract;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.repository.ContractRepository;
import com.example.ijara.service.impl.ContractPdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pdf")
@RequiredArgsConstructor
@Tag(name = "PDF Shartnomalar", description = "Ijara shartnomalarini PDF formatda yuklab olish")
public class PdfController {

    private final ContractRepository contractRepository;
    private final ContractPdfService contractPdfService;

    @GetMapping("/contract/{contractId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@securityService.isContractOwnerOrLessee(#contractId) or hasRole('ADMIN')")
    @Operation(summary = "Shartnomani PDF sifatida yuklab olish")
    public ResponseEntity<Resource> downloadContractPdf(
            @Parameter(description = "Shartnoma ID") 
            @PathVariable UUID contractId
    ) {
        Contract contract = contractRepository.findByIdAndActiveTrue(contractId)
                .orElseThrow(() -> new DataNotFoundException("Shartnoma topilmadi yoki mavjud emas"));

        ByteArrayInputStream pdfStream = contractPdfService.generateContractPdf(contract);

        InputStreamResource resource = new InputStreamResource(pdfStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=ijara-shartnomasi-" + contractId + ".pdf");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}