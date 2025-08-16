package com.example.ijara.repository;

import com.example.ijara.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByTelegramChatId(String chatId);
    Optional<User> findByUsername(String username);

    @Query("""
    SELECT u FROM User u
    WHERE (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :field, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :field, '%')))
      AND u.role = :role
    """)
    Page<User> search(
            @Param("field") String field,
            @Param("role") String role,
            Pageable pageable
    );
}
