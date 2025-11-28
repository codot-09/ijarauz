package com.example.ijara.specification;

import com.example.ijara.entity.Contract;
import com.example.ijara.entity.enums.ContractStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class ContractSpecification {

    public static Specification<Contract> filter(UUID userId, String productName, ContractStatus statusEnum) {
        return (root, query, cb) -> {
            Specification<Contract> spec = (root1, query1, cb1) -> cb1.isTrue(root.get("active"));

            if (userId != null) {
                spec = spec.and((root1, query1, cb1) ->
                        cb1.or(
                                cb1.equal(root.get("lessee").get("id"), userId),
                                cb1.equal(root.get("owner").get("id"), userId)
                        )
                );
            }

            if (productName != null && !productName.isEmpty()) {
                spec = spec.and((root1, query1, cb1) ->
                        cb1.like(cb.lower(root.get("product").get("name")), "%" + productName.toLowerCase() + "%")
                );
            }

            if (statusEnum != null) {
                spec = spec.and((root1, query1, cb1) ->
                        cb1.equal(root.get("contractStatus"), statusEnum)
                );
            }

            return spec.toPredicate(root, query, cb);
        };
    }

    public static Specification<Contract> finishedContracts(LocalDateTime now) {
        return (root, query, cb) ->
                cb.and(
                        cb.isTrue(root.get("active")),
                        cb.lessThan(root.get("endDateTime"), now),
                        cb.equal(root.get("contractStatus"), ContractStatus.ACTIVE)
                );
    }

    public static Specification<Contract> activeAndExpired(LocalDateTime now) {
        return (root, query, cb) ->
                cb.and(
                        cb.isTrue(root.get("active")),
                        cb.lessThan(root.get("endDateTime"), now),
                        cb.equal(root.get("contractStatus"), ContractStatus.ACTIVE)
                );
    }
}
