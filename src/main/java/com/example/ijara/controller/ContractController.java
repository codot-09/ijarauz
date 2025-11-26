package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqContract;
import com.example.ijara.dto.response.ResContract;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ContractStatus;
import com.example.ijara.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Tag(name = "Ijara shartnomalari", description = "Shartnoma yaratish, tasdiqlash, ko‘rish va boshqarish")
@SecurityRequirement(name = "bearerAuth")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @Operation(summary = "Yangi ijara shartnomasi yaratish (ijarachi tomonidan)")
    public ResponseEntity<ApiResponse<String>> createContract(
            @AuthenticationPrincipal User lessee,
            @Valid @RequestBody ReqContract request
    ) {
        return ResponseEntity.ok(contractService.saveContract(lessee, request));
    }

    @PutMapping("/{contractId}")
    @Operation(summary = "O‘z shartnomasini tahrirlash (faqat PENDING holatda)")
    public ResponseEntity<ApiResponse<String>> updateContract(
            @Parameter(description = "Tahrirlanadigan shartnoma ID")
            @PathVariable UUID contractId,
            @AuthenticationPrincipal User lessee,
            @Valid @RequestBody ReqContract request
    ) {
        return ResponseEntity.ok(contractService.updateContract(contractId, lessee, request));
    }

    @PatchMapping("/{contractId}/status")
    @Operation(
        summary = "Shartnoma holatini o‘zgartirish (faqat egasi)",
 description = "approved=true → ACTIVE, approved=false → CANCELLED"
    )
    public ResponseEntity<ApiResponse<String>> updateContractStatus(
            @Parameter(description = "Shartnoma ID")
            @PathVariable UUID contractId,
            @Parameter(description = "true = tasdiqlash, false = rad etish")
            @RequestParam boolean approved,
            @AuthenticationPrincipal User owner
    ) {
        return ResponseEntity.ok(contractService.updateStatusContract(contractId, approved, owner));
    }

    @DeleteMapping("/{contractId}")
    @Operation(summary = "O‘z shartnomasini o‘chirish (faqat PENDING holatda)")
    public ResponseEntity<ApiResponse<String>> deleteContract(
            @Parameter(description = "O‘chiriladigan shartnoma ID")
            @PathVariable UUID contractId,
            @AuthenticationPrincipal User lessee
    ) {
        return ResponseEntity.ok(contractService.deleteContract(lessee, contractId));
    }

    @GetMapping
    @Operation(
        summary = "Shartnomalarni filter qilib ko‘rish",
        description = "Oddiy user — faqat o‘ziniki, ADMIN — hammasinikini ko‘radi"
    )
    public ResponseEntity<ApiResponse<ResPageable>> getMyContracts(
            @Parameter(description = "Mahsulot nomi bo‘yicha qidiruv")
            @RequestParam(required = false) String productName,

            @Parameter(description = "Shartnoma holati bo‘yicha filter")
            @RequestParam(required = false) ContractStatus status,

            @Parameter(description = "Sahifa raqami")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Sahifa hajmi")
            @RequestParam(defaultValue = "10") int size,

            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
            contractService.getAllContractsByUser(user, productName, status, page, size)
        );
    }

    @GetMapping("/{contractId}")
    @Operation(summary = "Bitta shartnomani to‘liq ma'lumotlari bilan ko‘rish")
    public ResponseEntity<ApiResponse<ResContract>> getContractById(
            @Parameter(description = "Shartnoma ID")
            @PathVariable UUID contractId
    ) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }
}