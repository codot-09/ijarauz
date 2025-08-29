package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.ProductDTO;
import com.example.ijara.dto.request.ReqProduct;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ProductCondition;
import com.example.ijara.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Product qushish",
            description = "productPriceType 'HOUR', 'MONTH', 'DAY', 'YEAR' qilib kiritiladi")
    public ResponseEntity<ApiResponse<String>> saveProduct(@AuthenticationPrincipal User user,
                                                           @RequestParam ProductCondition productCondition,
                                                           @RequestBody ReqProduct reqProduct){
        return ResponseEntity.ok(productService.addProduct(user, reqProduct, productCondition));
    }



    @PutMapping("/{id}")
    @Operation(summary = "Productni update qilish uchun",
            description = "productPriceType 'HOUR', 'MONTH', 'DAY', 'YEAR' qilib kiritiladi")
    public ResponseEntity<ApiResponse<String>> updateProduct(@PathVariable UUID id,
                                                             @AuthenticationPrincipal User user,
                                                             @RequestParam ProductCondition productCondition,
                                                             @RequestBody ReqProduct reqProduct){
        return ResponseEntity.ok(productService.updateProduct(id, user, reqProduct, productCondition));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Productni uchirish uchun")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID id,
                                                             @AuthenticationPrincipal User user){
        return ResponseEntity.ok(productService.deleteProduct(id, user));
    }


    @GetMapping
    @Operation(summary = "Barcha productlarni kurish uchun va filter api",
               description = "Agar barcha parametrlar bush yuborilsa barchasi keladi")
    public ResponseEntity<ApiResponse<ResPageable>> searchProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false, defaultValue = "true") boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(productService.getAllProduct(name, categoryName, active, page, size));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Productni bittasini kurish id buyicha")
    public ResponseEntity<ApiResponse<ProductDTO>> getOneProduct(@PathVariable UUID id){
        return ResponseEntity.ok(productService.getProduct(id));
    }
}
