package com.example.ijara.specification;

import com.example.ijara.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> filter(String name, UUID categoryId) {
        return (root, query, cb) -> {
            Predicate predicate = cb.isTrue(root.get("active"));

            if (name != null && !name.isEmpty()) {
                predicate = cb.and(
                        predicate,
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
                );
            }

            if (categoryId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("category").get("id"), categoryId)
                );
            }

            return predicate;
        };
    }
}
