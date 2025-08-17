package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.ProductDTO;
import com.example.ijara.dto.request.ReqProduct;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ProductCondition;
import com.example.ijara.entity.enums.ProductType;

import java.util.UUID;

public interface ProductService {
    ApiResponse<String> addProduct(User user, ReqProduct reqProduct, ProductCondition productCondition, ProductType productType);
    ApiResponse<String> updateProduct(UUID id, User user, ReqProduct reqProduct, ProductCondition productCondition, ProductType productType);
    ApiResponse<String> deleteProduct(UUID id, User user);
    ApiResponse<ResPageable> getAllProduct(String name, ProductType productType, int page, int size);
    ApiResponse<ProductDTO> getProduct(UUID id);
}
