package com.example.ijara.repository;

import com.example.ijara.entity.Product;
import com.example.ijara.entity.ProductPrice;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, UUID> {

    List<ProductPrice> findAllByProductId(UUID id);
}
