package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.service.impl.CloudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Fayllar", description = "Rasm va hujjatlarni yuklash")
public class FileController {

    private final CloudService cloudService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Bitta rasm yuklash")
    public ResponseEntity<ApiResponse<String>> uploadSingle(
            @Parameter(description = "Yuklanadigan rasm (JPG, PNG, WEBP)") 
            @NotNull(message = "Fayl bo‘sh bo‘lmasligi kerak")
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String url = cloudService.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.success(url));
    }
}