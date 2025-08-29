package com.example.ijara.mapper;


import com.example.ijara.dto.CategoryDTO;
import com.example.ijara.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDTO toDto(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
