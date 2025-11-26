package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.IdList;
import com.example.ijara.dto.NotificationDTO;
import com.example.ijara.dto.response.ResNotification;
import com.example.ijara.entity.Notification;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.repository.NotificationRepository;
import com.example.ijara.repository.UserRepository;
import com.example.ijara.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse<String> adminSendNotificationToAll(ResNotification req) {
        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() != UserRole.ADMIN)
                .toList();

        if (users.isEmpty()) {
            return ApiResponse.error("Hech qanday foydalanuvchi topilmadi");
        }

        List<Notification> notifications = users.stream()
                .map(user -> buildNotification(req, user))
                .toList();

        notificationRepository.saveAll(notifications);
        return ApiResponse.success("Barcha foydalanuvchilarga xabar muvaffaqiyatli yuborildi");
    }

    @Override
    public ApiResponse<String> createNotification(UUID userId, ResNotification req) {
        User user = getUserById(userId);

        Notification notification = buildNotification(req, user);
        notificationRepository.save(notification);

        return ApiResponse.success("Xabar muvaffaqiyatli yaratildi");
    }

    @Override
    public ApiResponse<List<NotificationDTO>> getMyNotifications(User user) {
        List<NotificationDTO> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toDto)
                .toList();

        return notifications.isEmpty()
                ? ApiResponse.error("Sizga hali hech qanday xabar kelgan emas")
                : ApiResponse.success(notifications);
    }

    @Override
    public ApiResponse<Long> getUnreadNotificationCount(User user) {
        long count = notificationRepository.countByUserIdAndReadFalse(user.getId());
        return ApiResponse.success(count);
    }

    @Override
    @Transactional
    public ApiResponse<String> markAsRead(IdList idList) {
        if (idList.getIds() == null || idList.getIds().isEmpty()) {
            return ApiResponse.error("Xabar ID lari ro‘yxati bo‘sh bo‘lmasligi kerak");
        }

        int updated = notificationRepository.markAsReadByIds(idList.getIds());

        return updated > 0
                ? ApiResponse.success(updated + " ta xabar o‘qilgan deb belgilandi")
                : ApiResponse.error("O‘qiladigan xabarlar topilmadi");
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteNotification(UUID notificationId, User user) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new DataNotFoundException("Xabar topilmadi yoki sizga tegishli emas"));

        notificationRepository.delete(notification);
        return ApiResponse.success("Xabar muvaffaqiyatli o‘chirildi");
    }

    // Helper methods
    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Foydalanuvchi topilmadi"));
    }

    private Notification buildNotification(ResNotification req, User user) {
        return Notification.builder()
                .title(req.getTitle())
                .message(req.getContent())
                .user(user)
                .read(false)
                .build();
    }

    private NotificationDTO toDto(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getUser().getId(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}