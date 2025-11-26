package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.ProductDTO;
import com.example.ijara.dto.request.ReqProduct;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.dto.response.ResProduct;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ProductCondition;

import java.util.List;
import java.util.UUID;

/**
 * Mahsulotlar bilan ishlash uchun asosiy xizmat interfeysi.
 */
public interface ProductService {

    /**
     * Yangi mahsulot qo‘shadi.
     *
     * @param owner     Mahsulot egasi (joriy foydalanuvchi)
     * @param req       Mahsulot ma'lumotlari va narx turlari
     * @param condition Mahsulot holati (NEW, USED, REFUR81SHED)
     * @return Muvaffaqiyat xabari
     */
    ApiResponse<String> addProduct(User owner, ReqProduct req, ProductCondition condition);

    /**
     * Mavjud mahsulotni yangilaydi (faqat egasi uchun).
     *
     * @param productId Yangilanadigan mahsulot ID
     * @param user      Joriy foydalanuvchi (egasi bo‘lishi shart)
     * @param req       Yangi ma'lumotlar
     * @param condition Yangi holat
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> updateProduct(UUID productId, User user, ReqProduct req, ProductCondition condition);

    /**
     * Mahsulotni o‘chiradi (soft delete — active = false).
     *
     * @param productId O‘chiriladigan mahsulot ID
     * @param user      Joriy foydalanuvchi (faqat egasi)
     * @return Muvaffaqiyat xabari
     */
    ApiResponse<String> deleteProduct(UUID productId, User user);

    /**
     * Faol mahsulotlarni sahifalab qaytaradi.
     * Qidiruv: nomi va kategoriyasi bo‘yicha.
     *
     * @param name         Mahsulot nomi bo‘yicha qidiruv (ixtiyoriy)
     * @param categoryId Kategoriya idsi bo‘yicha filter (ixtiyoriy)
     * @param page         Sahifa raqami (0 dan boshlanadi)
     * @param size         Har sahifadagi elementlar soni
     * @return Sahifalangan mahsulotlar ro‘yxati
     */
    ApiResponse<ResPageable> getAllProducts(String name, UUID categoryId, int page, int size);

    /**
     * Bitta mahsulot haqida to‘liq ma'lumot qaytaradi.
     * Izohlar, narxlar, o‘rtacha reyting bilan birga.
     *
     * @param productId Mahsulot ID
     * @return To‘liq mahsulot ma'lumotlari
     */
    ApiResponse<ProductDTO> getProductById(UUID productId);

    /**
     *
     * O'z mahsulotlarini listini olish
     *
     * @param user mahsulot egasi
     * @return Mahsulotlar isti
     */
    ApiResponse<List<ResProduct>> getMyProducts(User user);
}
