package com.example.ijara.repository;

import com.example.ijara.dto.response.ResProduct;
import com.example.ijara.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = """
    select p.id,
           p.name,
           p.description,
           p.product_type,
           p.product_condition,
           coalesce(avg(f.rating), 0) as rating,
           p.lat,
           p.lng,
           (select pp.price
            from product_price pp
            where pp.product_id = p.id and pp.product_price_type = 'DAY') as price,
           array_agg(imgs.img_urls) as img_urls
    from product p
    left join feedback f on f.product_id = p.id
    left join product_img_urls imgs on imgs.product_id = p.id
    where (:name is null or lower(p.name) like lower(concat('%', :name, '%')))
      and (:type is null or lower(p.product_type) like lower(concat('%', :type, '%')))
    group by p.id, p.created_at
    order by p.created_at desc
""", nativeQuery = true)
    Page<ResProduct> searchProduct(@Param("name") String name,
                                 @Param("type") String type,
                                 Pageable pageable);

}
