package com.example.ijara.repository;

import com.example.ijara.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    Optional<Feedback> findByIdAndUserId(UUID id, UUID userId);
    List<Feedback> findAllByUserId(UUID userId);
    List<Feedback> findAllByProductId(UUID productId);
}
