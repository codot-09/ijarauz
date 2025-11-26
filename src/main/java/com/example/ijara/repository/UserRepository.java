package com.example.ijara.repository;

import com.example.ijara.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByTelegramChatId(String telegramChatId);

    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUsernameAndActiveTrue(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByTelegramChatIdAndActiveTrue(String telegramChatId);

    @Query("SELECT u FROM User u WHERE u.active = true")
    Page<User> findAllActive(Pageable pageable);

    @Query("""
          SELECT u FROM User u
          WHERE u.active = true
            AND (
                  :query IS NULL
                  OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND (:role IS NULL OR u.role = :role)
          """)
    Page<User> searchUsers(
            @Param("query") String query,
            @Param("role") String role,
            Pageable pageable
    );

}