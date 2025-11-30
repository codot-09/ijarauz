package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.ProductDTO;
import com.example.ijara.dto.request.ReqProduct;
import com.example.ijara.dto.response.ProductMapView;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.dto.response.ResProduct;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ProductCondition;
import com.example.ijara.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Mahsulotlar", description = "Mahsulot qo‘shish, yangilash, o‘chirish va qidirish")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Yangi mahsulot qo‘shish")
    public ResponseEntity<ApiResponse<String>> addProduct(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Mahsulot holati: NEW, USED, REFURBISHED") 
            @RequestParam ProductCondition condition,
            @Valid @RequestBody ReqProduct req
    ) {
        return ResponseEntity.ok(productService.addProduct(user, req, condition));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mahsulotni yangilash (faqat egasi)")
    public ResponseEntity<ApiResponse<String>> updateProduct(
            @Parameter(description = "Yangilanadigan mahsulot ID") 
            @PathVariable UUID id,
            @AuthenticationPrincipal User user,
            @Parameter(description = "Yangi holat: NEW, USED, REFURBISHED") 
            @RequestParam ProductCondition condition,
            @Valid @RequestBody ReqProduct req
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, user, req, condition));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mahsulotni o‘chirish (soft delete, faqat egasi)")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @Parameter(description = "O‘chiriladigan mahsulot ID") 
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(productService.deleteProduct(id, user));
    }

    @GetMapping
    @Operation(summary = "Mahsulotlarni qidirish va sahifalash")
    public ResponseEntity<ApiResponse<ResPageable>> getAllProducts(
            @Parameter(description = "Mahsulot nomi bo‘yicha qidiruv") 
            @RequestParam(required = false) String name,

            @Parameter(description = "Kategoriya nomi bo‘yicha filter") 
            @RequestParam(required = false) UUID categoryId,

            @Parameter(description = "Sahifa raqami (0 dan boshlanadi)") 
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Har sahifadagi elementlar soni") 
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.getAllProducts(name, categoryId, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Bitta mahsulotni to‘liq ma'lumotlari bilan olish")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @Parameter(description = "Mahsulot ID") 
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/my-products")
    @Operation(summary = "O'z mahsulotlarini ko'rish")
    public ResponseEntity<ApiResponse<List<ResProduct>>> getOwnProducts(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(productService.getMyProducts(user));
    }

    @GetMapping("/nearby")
    @Operation(summary = "O'ziga yaqin mahsulotlarni ko'rish")
    public ResponseEntity<ApiResponse<List<ProductMapView>>> getNearbyProducts(
            @Parameter(description = "Foydalanuvchi latitudi")
            @RequestParam double lat,

            @Parameter(description = "Foydalanuvchi longitudi")
            @RequestParam double lng
    ){
        return ResponseEntity.ok(productService.getNearbyProducts(lat, lng));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Top reytingli mahsulotlarni olish")
    public ResponseEntity<ApiResponse<ResPageable>> getTopRated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(productService.getTopRatedProducts(page, size));
    }
}
