package com.example.ijara.repository;

import com.example.ijara.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    @Query(value = """
            select c.* from contract c left join product p on p.id = c.product_id where
                (:productName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%'))) and
                (:userId IS NULL OR c.lessee_id = :userId) and
                (:userId IS NULL OR c.owner_id = :userId) and
                (:status IS NULL OR c.contract_status = :status) and c.active = true
                order by c.created_at desc
    """, nativeQuery = true)
    Page<Contract> searchContract(@Param("userId") UUID userId,
                                  @Param("productName") String productName,
                                  @Param("status") String status, Pageable pageable);

    Optional<Contract> findByIdAndActiveTrue(UUID id);

    @Query(value = """
            select c.* from contract c where c.active = true and c.contract_status = 'ACTIVE' and c.end_date_time <= :date
        """, nativeQuery = true)
    List<Contract> findFinishedContracts(@Param("date") LocalDateTime date);
}
