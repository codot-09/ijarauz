package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.CategoryDTO;
import com.example.ijara.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    @GetMapping
    @Operation(summary = "Barcha categorylarni kurish")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }


    @GetMapping("/{categoryId}")
    @Operation(summary = "Bitta categoryni kurish")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryService.getOneCategoryId(categoryId));
    }

    @PostMapping
    @Operation(summary = "Category saqlash uchun")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> saveCategory(@RequestParam String categoryName) {
        return ResponseEntity.ok(categoryService.saveCategory(categoryName));
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Categroyni update qilish uchun")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateCategory(@PathVariable UUID categoryId, @RequestParam String categoryName) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryName));
    }


    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Categoryni uchirish uchun")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }
}
