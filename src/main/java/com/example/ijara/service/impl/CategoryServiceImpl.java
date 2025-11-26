package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.CategoryDTO;
import com.example.ijara.entity.Category;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.mapper.CategoryMapper;
import com.example.ijara.repository.CategoryRepository;
import com.example.ijara.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public ApiResponse<String> saveCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            return ApiResponse.error("Bu nomli kategoriya allaqachon mavjud");
        }
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
        return ApiResponse.success("Muvaffaqiyatli saqlandi");
    }

    @Override
    public ApiResponse<String> updateCategory(UUID id, String name) {
        Category category = getCategory(id);
        category.setName(name);
        categoryRepository.save(category);
        return ApiResponse.success("Muvaffaqiyatli yangilandi");
    }

    @Override
    public ApiResponse<String> deleteCategory(UUID id) {
        Category category = getCategory(id);
        category.setDeleted(true);
        categoryRepository.save(category);
        return ApiResponse.success("Muvaffaqiyatli o‘chirildi");
    }

    @Override
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();

        return categories.isEmpty()
                ? ApiResponse.error("Kategoriyalar topilmadi")
                : ApiResponse.success(categories);
    }

    @Override
    public ApiResponse<CategoryDTO> getCategoryById(UUID id) {
        Category category = getCategory(id);
        return ApiResponse.success(categoryMapper.toDto(category));
    }

    private Category getCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Kategoriya topilmadi"));
    }
}