package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqContract;
import com.example.ijara.dto.response.ResContract;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ContractStatus;
import java.util.UUID;

/**
 * Shartnoma (Contract) bilan ishlash uchun asosiy xizmat interfeysi.
 */
public interface ContractService {

    /**
     * Yangi ijara shartnomasini yaratadi.
     *
     * @param user       Joriy foydalanuvchi (ijaraga oluvchi)
     * @param reqContract Shartnoma ma'lumotlari
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> saveContract(User user, ReqContract reqContract);

    /**
     * Mavjud shartnomani yangilaydi (faqat PENDING holatdagilar uchun).
     *
     * @param contractId Shartnoma ID
     * @param user       Joriy foydalanuvchi (ijaraga oluvchi)
     * @param reqContract Yangi ma'lumotlar
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> updateContract(UUID contractId, User user, ReqContract reqContract);

    /**
     * Shartnomani o‘chiradi (soft delete — active = false).
     *
     * @param user       Joriy foydalanuvchi (ijaraga oluvchi)
     * @param contractId O‘chiriladigan shartnoma ID
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> deleteContract(User user, UUID contractId);

    /**
     * Shartnoma holatini tasdiqlaydi yoki rad etadi (faqat egasi uchun).
     *
     * @param contractId Shartnoma ID
     * @param approved   true — tasdiqlash, false — rad etish
     * @param owner      Joriy foydalanuvchi (mahsulot egasi)
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> updateStatusContract(UUID contractId, boolean approved, User owner);

    /**
     * Foydalanuvchining barcha shartnomalarini sahifalab qaytaradi.
     *
     * @param user          Joriy foydalanuvchi (ADMIN — barchasini ko‘radi)
     * @param productName   Mahsulot nomi bo‘yicha qidiruv (ixtiyoriy)
     * @param status        Shartnoma holati bo‘yicha filter (ixtiyoriy)
     * @param page          Sahifa raqami (0 dan boshlab)
     * @param size          Har sahifadagi elementlar soni
     * @return Sahifalangan shartnomalar ro‘yxati
     */
    ApiResponse<ResPageable> getAllContractsByUser(
            User user,
            String productName,
            ContractStatus status,
            int page,
            int size
    );

    /**
     * Bitta shartnoma haqida to‘liq ma'lumot qaytaradi.
     *
     * @param contractId Shartnoma ID
     * @return Shartnoma tafsilotlari
     */
    ApiResponse<ResContract> getContractById(UUID contractId);
}