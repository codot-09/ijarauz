package com.example.ijara.repository;

import com.example.ijara.entity.Product;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByIdAndActiveTrue(UUID id);

    Page<Product> findAllByActiveTrue(org.springframework.data.jpa.domain.Specification<Product> spec,Pageable pageable);

    Optional<Product> findByIdAndOwnerId(UUID id, UUID ownerId);

    Optional<Product> findByIdAndOwnerIdAndActiveTrue(UUID id, UUID ownerId);

    List<Product> findByOwner(User user);

    @Modifying
    @Query("UPDATE Product p SET p.active = false " +
           "WHERE p.createdAt < :date " +
           "AND p.owner.role != :companyRole")
    void deactivateProductsOlderThanAndNotCompany(
            @Param("date") LocalDateTime date,
            @Param("companyRole") UserRole companyRole
    );

    @Query(value = """
    SELECT *
    FROM Product p
    WHERE (6371 * acos(
        cos(radians(:lat)) 
        * cos(radians(p.lat)) 
        * cos(radians(p.lng) - radians(:lng)) 
        + sin(radians(:lat)) 
        * sin(radians(p.lat))
    )) < :radiusKm
    LIMIT :limit
""", nativeQuery = true)
    List<Product> findNearbyProducts(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm,
            @Param("limit") int limit
    );

}