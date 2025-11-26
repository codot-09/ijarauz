package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.CategoryDTO;
import com.example.ijara.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Kategoriyalar", description = "Kategoriyalarni boshqarish (ADMIN) va ko‘rish (hamma)")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Barcha faol kategoriyalarni olish")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Bitta kategoriyani ID bo‘yicha olish")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(
            @Parameter(description = "Kategoriya ID")
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi kategoriya qo‘shish (faqat ADMIN)")
    public ResponseEntity<ApiResponse<String>> createCategory(
            @Parameter(description = "Kategoriya nomi (masalan: Avtomobil, Uy-joy, Texnika)")
            @NotBlank(message = "Kategoriya nomi bo‘sh bo‘lmasligi kerak")
            @RequestParam String name
    ) {
        return ResponseEntity.ok(categoryService.saveCategory(name.trim()));
    }

    @PutMapping("/{categoryId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kategoriya nomini yangilash (faqat ADMIN)")
    public ResponseEntity<ApiResponse<String>> updateCategory(
            @Parameter(description = "Yangilanadigan kategoriya ID")
            @PathVariable UUID categoryId,

            @Parameter(description = "Yangi kategoriya nomi")
            @NotBlank(message = "Kategoriya nomi bo‘sh bo‘lmasligi kerak")
            @RequestParam String name
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, name.trim()));
    }

    @DeleteMapping("/{categoryId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kategoriyani o‘chirish (soft delete, faqat ADMIN)")
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @Parameter(description = "O‘chiriladigan kategoriya ID")
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }
}