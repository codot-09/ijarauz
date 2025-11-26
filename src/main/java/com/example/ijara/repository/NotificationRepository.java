package com.example.ijara.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.ijara.entity.Notification;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Query("UPDATE Notification n SET n.read = true WHERE n.id IN :ids")
    @Modifying
    int markAsReadByIds(@Param("ids") List<UUID> ids);

    long countByUserIdAndReadFalse(UUID userId);

    List<Notification> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
}
