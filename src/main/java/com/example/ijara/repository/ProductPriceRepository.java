package com.example.ijara.repository;

import com.example.ijara.entity.ProductPrice;
import com.example.ijara.entity.enums.ProductCondition;
import com.example.ijara.entity.enums.ProductPriceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, UUID> {

    List<ProductPrice> findAllByProductId(UUID id);

    ProductPrice findByProductIdAndProductPriceType(UUID productId, ProductPriceType type);

    Optional<ProductPrice> findByProductPriceType(ProductPriceType productPriceType);

}
