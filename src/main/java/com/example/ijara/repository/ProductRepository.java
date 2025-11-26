package com.example.ijara.repository;

import com.example.ijara.entity.Product;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndActiveTrue(UUID id);

    Optional<Product> findByIdAndOwnerId(UUID id, UUID ownerId);

    Optional<Product> findByIdAndOwnerIdAndActiveTrue(UUID id, UUID ownerId);

    List<Product> findByOwnerAndActiveTrue(User user);

    @Query("""
       SELECT p FROM Product p
       WHERE p.active = :active
         AND (:name IS NULL OR p.name ILIKE CONCAT('%', :name, '%'))
         AND (:categoryId IS NULL OR p.category.id = :categoryId)
       """)
    Page<Product> searchProduct(
            @Param("name") String name,
            @Param("categoryId") UUID categoryId,
            @Param("active") boolean active,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Product p SET p.active = false " +
           "WHERE p.createdAt < :date " +
           "AND p.owner.role != :companyRole")
    void deactivateProductsOlderThanAndNotCompany(
            @Param("date") LocalDateTime date,
            @Param("companyRole") UserRole companyRole
    );
}