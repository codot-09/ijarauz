package com.example.ijara.repository;

import com.example.ijara.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByTelegramChatId(String chatId);
    Optional<User> findByUsername(String username);
}
