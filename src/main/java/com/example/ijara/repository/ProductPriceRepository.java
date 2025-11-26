package com.example.ijara.repository;

import com.example.ijara.entity.ProductPrice;
import com.example.ijara.entity.enums.ProductPriceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, UUID> {

    List<ProductPrice> findAllByProductId(UUID productId);

    Optional<ProductPrice> findByProductIdAndProductPriceType(UUID productId, ProductPriceType type);

    @Query("SELECT pp.price FROM ProductPrice pp " +
           "WHERE pp.product.id = :productId AND pp.productPriceType = :type AND pp.active = true")
    Double findPriceByProductIdAndType(@Param("productId") UUID productId, @Param("type") ProductPriceType type);

    @Modifying
    void deleteByProductId(UUID productId);

    @Modifying
    @Query("UPDATE ProductPrice pp SET pp.active = false WHERE pp.product.id = :productId")
    void deactivateByProductId(@Param("productId") UUID productId);
}