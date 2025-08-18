package com.example.ijara.repository;

import com.example.ijara.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = """
    select p.* from product p where
    (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%'))) and
    (:type IS NULL OR LOWER(p.product_type) LIKE LOWER(CONCAT('%',:type,'%'))) and p.active = :active order by p.created_at desc
""", nativeQuery = true)
    Page<Product> searchProduct(@Param("name") String name,
                                 @Param("type") String type,
                                 @Param("active") boolean active,
                                 Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(UUID id);

}
