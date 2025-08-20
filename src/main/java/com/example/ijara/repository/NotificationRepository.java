package com.example.ijara.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ijara.entity.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByUserId(UUID userId);

    Integer countAllByUserIdAndReadFalse(UUID userId);
}
