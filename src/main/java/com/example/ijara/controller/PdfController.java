package com.example.ijara.controller;

import com.example.ijara.entity.Contract;
import com.example.ijara.repository.ContractRepository;
import com.example.ijara.service.impl.ContractPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfController {
    private final ContractRepository contractRepository;
    private final ContractPdfService contractPdfService;

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadContract(@PathVariable UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shartnoma topilmadi"));

        ByteArrayInputStream bis = contractPdfService.generateContractPdf(contract);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=contract-" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }
}
