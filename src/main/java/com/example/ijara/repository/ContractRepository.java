package com.example.ijara.repository;

import com.example.ijara.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    Optional<Contract> findByIdAndActiveTrue(UUID id);

    @Query("""
            SELECT c FROM Contract c
            WHERE c.active = true
              AND (:userId IS NULL OR c.lessee.id = :userId OR c.owner.id = :userId)
              AND (:productName IS NULL OR LOWER(c.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
              AND (:status IS NULL OR c.contractStatus = :statusEnum)
            """)
    Page<Contract> searchContract(
            @Param("userId") UUID userId,
            @Param("productName") String productName,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT c FROM Contract c WHERE c.active = true AND c.endDateTime < :now AND c.contractStatus = 'ACTIVE'")
    List<Contract> findFinishedContracts(@Param("now") LocalDateTime now);

    @Query("""
            SELECT c FROM Contract c
            WHERE c.active = true
              AND c.contractStatus = 'ACTIVE'
              AND c.endDateTime < :now
            """)
    List<Contract> findActiveAndExpired(@Param("now") LocalDateTime now);
}