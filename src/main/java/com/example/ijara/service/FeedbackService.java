package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqFeedback;
import com.example.ijara.entity.User;
import java.util.List;
import java.util.UUID;

/**
 * Foydalanuvchi izohlari (feedback) bilan ishlash uchun xizmat interfeysi.
 */
public interface FeedbackService {

    /**
     * Yangi izoh qoldiradi.
     *
     * @param user   Joriy foydalanuvchi (izoh muallifi)
     * @param req    Izoh va reyting ma'lumotlari
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> saveFeedback(User user, ReqFeedback req);

    /**
     * O‘z izohini yangilaydi.
     *
     * @param user       Joriy foydalanuvchi (izoh egasi)
     * @param feedbackId Yangilanadigan izoh ID
     * @param req        Yangi ma'lumotlar
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> updateFeedback(User user, UUID feedbackId, ReqFeedback req);

    /**
     * O‘z izohini o‘chiradi.
     *
     * @param user       Joriy foydalanuvchi (izoh egasi)
     * @param feedbackId O‘chiriladigan izoh ID
     * @return Muvaffaqiyat yoki xato xabari
     */
    ApiResponse<String> deleteFeedback(User user, UUID feedbackId);

    /**
     * Joriy foydalanuvchi qoldirgan barcha izohlarni qaytaradi.
     *
     * @param user Joriy foydalanuvchi
     * @return Foydalanuvchi izohlari ro‘yxati
     */
    ApiResponse<List<ReqFeedback>> getMyFeedback(User user);
}