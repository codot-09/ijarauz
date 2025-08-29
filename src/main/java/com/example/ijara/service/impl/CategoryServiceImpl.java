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
    public ApiResponse<String> saveCategory(String name){
        boolean b = categoryRepository.existsByName(name);
        if(b){
            return ApiResponse.error("Bu nomli categoriya allaqachon bor");
        }
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
        return ApiResponse.success("Success");
    }


    @Override
    public ApiResponse<String> updateCategory(UUID id,String name){
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        category.setName(name);
        categoryRepository.save(category);
        return ApiResponse.success( "Success");
    }


    @Override
    public ApiResponse<String> deleteCategory(UUID id){
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );
        category.setDeleted(true);
        categoryRepository.save(category);
        return ApiResponse.success( "Success");
    }


    @Override
    public ApiResponse<List<CategoryDTO>> getAllCategories(){
        List<Category> all = categoryRepository.findAll();
        if(all.isEmpty()){
            return ApiResponse.error("Not found");
        }
        List<CategoryDTO> list = all.stream().map(categoryMapper::toDto).toList();
        return ApiResponse.success( list);
    }



    @Override
    public ApiResponse<CategoryDTO> getOneCategoryId(UUID id){
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );
        return ApiResponse.success(categoryMapper.toDto(category));
    }
}
