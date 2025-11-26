package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.IdList;
import com.example.ijara.dto.NotificationDTO;
import com.example.ijara.dto.response.ResNotification;
import com.example.ijara.entity.User;
import java.util.List;
import java.util.UUID;

/**
 * Xabarlar (notification) tizimi uchun xizmat interfeysi.
 */
public interface NotificationService {

    /**
     * Admin barcha foydalanuvchilarga bir vaqtning o‘zida xabar yuboradi.
     *
     * @param req Xabar ma'lumotlari (title + content)
     * @return Muvaffaqiyatli yuborilganlar soni yoki xabar
     */
    ApiResponse<String> adminSendNotificationToAll(ResNotification req);

    /**
     * Bitta foydalanuvchiga shaxsiy xabar yaratadi.
     *
     * @param userId Xabar oluvchi foydalanuvchi ID
     * @param req    Xabar ma'lumotlari
     * @return Muvaffaqiyat xabari
     */
    ApiResponse<String> createNotification(UUID userId, ResNotification req);

    /**
     * Joriy foydalanuvchining barcha xabarlarini (oxirgi yaratilgan birinchi) qaytaradi.
     *
     * @param user Joriy foydalanuvchi
     * @return Xabarlar ro‘yxati
     */
    ApiResponse<List<NotificationDTO>> getMyNotifications(User user);

    /**
     * O‘qilmagan xabarlar sonini qaytaradi.
     *
     * @param user Joriy foydalanuvchi
     * @return O‘qilmagan xabarlar soni
     */
    ApiResponse<Long> getUnreadNotificationCount(User user);

    /**
     * Bir nechta xabarni o‘qilgan deb belgilaydi.
     *
     * @param idList O‘qiladigan xabarlar ID lari ro‘yxati
     * @return Yangilangan xabarlar soni
     */
    ApiResponse<String> markAsRead(IdList idList);

    /**
     * O‘z xabarini o‘chiradi.
     *
     * @param notificationId O‘chiriladigan xabar ID
     * @param user           Joriy foydalanuvchi (faqat o‘z xabarini o‘chirishi mumkin)
     * @return Muvaffaqiyat xabari
     */
    ApiResponse<String> deleteNotification(UUID notificationId, User user);
}