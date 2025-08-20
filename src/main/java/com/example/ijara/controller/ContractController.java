package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqContract;
import com.example.ijara.dto.response.ResContract;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.entity.Contract;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ContractStatus;
import com.example.ijara.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping
    @Operation(summary = "Contractni ijarachi tomonidan yaratish")
    public ResponseEntity<ApiResponse<String>> saveContract(@AuthenticationPrincipal User user,
                                                            @RequestBody ReqContract reqContract) {
        return ResponseEntity.ok(contractService.saveContract(user,reqContract));
    }


    @PutMapping("/{contractId}")
    @Operation(summary = "Ijaraga oluvchi o'zi yaratgan contractni tahrirlashi uchun")
    public ResponseEntity<ApiResponse<String>> updateContract(@PathVariable UUID contractId,
                                                              @AuthenticationPrincipal User user,
                                                              @RequestBody ReqContract reqContract) {
        return ResponseEntity.ok(contractService.updateContact(contractId,user,reqContract));
    }

    @PutMapping("/status/{contractId}")
    @Operation(summary = "Ijaraga beruvchi shartnomani tasdiqlashi uchun",
               description = "Agar status true yuborsa tasdiqlanadi, Agar status false yuborsa bekor qilinadi")
    public ResponseEntity<ApiResponse<String>> updateStatusContract(@PathVariable UUID contractId,
                                                                    @AuthenticationPrincipal User owner,
                                                                    @RequestParam boolean status){
        return ResponseEntity.ok(contractService.updateStatusContract(contractId,status,owner));
    }


    @DeleteMapping("/{contractId}")
    @Operation(summary = "Ijarachi o'zi yaratgan shartnomani o'chirishi uchun")
    public ResponseEntity<ApiResponse<String>> deleteContract(@PathVariable UUID contractId,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contractService.deleteContract(user,contractId));
    }


    @GetMapping
    @Operation(summary = "Contract filter uchun",
            description = "Barcha userlar o'zining contractlarni ko'rish, Agar admin bulsa hammanikini kuradi")
    public ResponseEntity<ApiResponse<ResPageable>> searchContract(@RequestParam(required = false) String productName,
                                                                   @RequestParam ContractStatus contractStatus,
                                                                   @AuthenticationPrincipal User user,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(contractService.getAllContractByUser(user,productName,contractStatus,page,size));
    }



    @GetMapping("/{contractId}")
    @Operation(summary = "Contractni id buyicha kurish")
    public ResponseEntity<ApiResponse<ResContract>> getContract(@PathVariable UUID contractId) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }
}
