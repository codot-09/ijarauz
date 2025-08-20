package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.IdList;
import com.example.ijara.dto.NotificationDTO;
import com.example.ijara.dto.response.ResNotification;
import com.example.ijara.entity.Notification;
import com.example.ijara.entity.User;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.repository.NotificationRepository;
import com.example.ijara.repository.UserRepository;
import com.example.ijara.service.NotificationService;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<?> adminSendNotificationAll(ResNotification resNotification) {
        for (User user : userRepository.findAllByRoleAndEnabledTrue()) {
            Notification notification = saveNotification(resNotification, user);
            notificationRepository.save(notification);
        }

        return ApiResponse.success("Notification successfully send");
    }

    @Override
    public ApiResponse<?> createNotification(UUID userId, ResNotification resNotification) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("User topilmadi")
        );

        Notification notification = Notification.builder()
                .title(resNotification.getTitle())
                .message(resNotification.getContent())
                .user(user)
                .read(false)
                .build();
        notificationRepository.save(notification);
        return ApiResponse.success("Notification created");
    }

    @Override
    public ApiResponse<?> getMyNotifications(User user) {
        List<NotificationDTO> list = notificationRepository.findAllByUserId(user.getId()).stream()
                .map(this::convertDtoToNotification).toList();

        return ApiResponse.success(list);
    }

    @Override
    public ApiResponse<?> getUnReadNotificationCount(User user) {
        Integer i = notificationRepository.countAllByUserIdAndReadFalse(user.getId());
        return ApiResponse.success(i);
    }

    @Override
    public ApiResponse<?> readNotification(IdList idList) {
        if (idList.getIds().isEmpty()){
            return ApiResponse.error("List bush bulmasin");
        }

        List<Notification> allById = notificationRepository.findAllById(idList.getIds());
        allById.forEach(notification -> {notification.setRead(true);
            notificationRepository.save(notification);});

        return ApiResponse.success("Successfully read notifications");
    }

    @Override
    public ApiResponse<?> deleteNotification(UUID id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Notification topilmadi")
        );

        notificationRepository.delete(notification);
        return ApiResponse.success("Notification deleted");
    }


    public NotificationDTO convertDtoToNotification(Notification notification)
    {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getUser().getId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }


    private Notification saveNotification(ResNotification resNotification, User user){
        return Notification.builder()
                .title(resNotification.getTitle())
                .message(resNotification.getContent())
                .user(user)
                .read(false)
                .build();
    }
}
