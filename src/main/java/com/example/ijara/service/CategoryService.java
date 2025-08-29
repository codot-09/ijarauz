package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    ApiResponse<String> saveCategory(String name);
    ApiResponse<String> updateCategory(UUID id, String name);
    ApiResponse<String> deleteCategory(UUID id);
    ApiResponse<List<CategoryDTO>> getAllCategories();
    ApiResponse<CategoryDTO> getOneCategoryId(UUID id);
}
