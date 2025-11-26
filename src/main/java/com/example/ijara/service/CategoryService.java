package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.CategoryDTO;
import java.util.List;
import java.util.UUID;

/**
 * Kategoriyalar bilan ishlash uchun xizmat interfeysi.
 */
public interface CategoryService {

    /**
     * Yangi kategoriya qo‘shadi.
     *
     * @param name Kategoriya nomi
     * @return Muvaffaqiyat yoki xato xabari (masalan, nom allaqachon mavjud)
     */
    ApiResponse<String> saveCategory(String name);

    /**
     * Mavjud kategoriyani yangilaydi.
     *
     * @param id   Yangilanadigan kategoriya ID
     * @param name Yangi nom
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> updateCategory(UUID id, String name);

    /**
     * Kategoriyani o‘chiradi (soft delete — deleted = true).
     *
     * @param id O‘chiriladigan kategoriya ID
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> deleteCategory(UUID id);

    /**
     * Barcha faol kategoriyalarni qaytaradi.
     *
     * @return Faol kategoriyalar ro‘yxati
     */
    ApiResponse<List<CategoryDTO>> getAllCategories();

    /**
     * Bitta kategoriyani ID bo‘yicha qaytaradi.
     *
     * @param id Kategoriya ID
     * @return Kategoriya ma'lumotlari
     */
    ApiResponse<CategoryDTO> getCategoryById(UUID id);
}