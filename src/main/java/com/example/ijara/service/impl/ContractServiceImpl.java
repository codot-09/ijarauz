package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqContract;
import com.example.ijara.dto.response.ResContract;
import com.example.ijara.dto.response.ResNotification;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.dto.response.ResProductDuration;
import com.example.ijara.entity.Contract;
import com.example.ijara.entity.Product;
import com.example.ijara.entity.ProductPrice;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ContractStatus;
import com.example.ijara.entity.enums.ProductPriceType;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.repository.ContractRepository;
import com.example.ijara.repository.ProductPriceRepository;
import com.example.ijara.repository.ProductRepository;
import com.example.ijara.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ContractRepository contractRepository;
    private final NotificationServiceImpl notificationService;

    @Override
    public ApiResponse<String> saveContract(User lessee, ReqContract req) {
        Product product = getActiveProduct(req.getProductId());

        double totalPrice = calculatePrice(req.getStartDate(), req.getEndDate(), product.getId());
        Contract contract = Contract.builder()
                .owner(product.getOwner())
                .lessee(lessee)
                .product(product)
                .price(totalPrice)
                .startDateTime(req.getStartDate())
                .endDateTime(req.getEndDate())
                .contractStatus(ContractStatus.PENDING)
                .active(true)
                .build();

        contractRepository.save(contract);

        notificationService.createNotification(
                product.getOwner().getId(),
                ResNotification.builder()
                        .title("Yangi ijara so‘rovi!")
                        .content(lessee.getUsername() + " sizning \"" + product.getName() + "\" mahsulotingizni ijaraga olmoqchi")
                        .build()
        );

        return ApiResponse.success("Shartnoma muvaffaqiyatli yaratildi");
    }

    @Override
    public ApiResponse<String> updateContract(UUID contractId, User user, ReqContract req) {
        Contract contract = getActiveContract(contractId);

        if (!contract.getLessee().getId().equals(user.getId())) {
            return ApiResponse.error("Bu shartnoma sizga tegishli emas");
        }

        if (contract.getContractStatus() != ContractStatus.PENDING) {
            return ApiResponse.error("Faqat kutayotgan shartnomalarni tahrirlash mumkin");
        }

        Product product = getActiveProduct(req.getProductId());
        double newPrice = calculatePrice(req.getStartDate(), req.getEndDate(), product.getId());

        contract.setProduct(product);
        contract.setStartDateTime(req.getStartDate());
        contract.setEndDateTime(req.getEndDate());
        contract.setPrice(newPrice);
        contractRepository.save(contract);

        return ApiResponse.success("Shartnoma muvaffaqiyatli yangilandi");
    }

    @Override
    public ApiResponse<String> deleteContract(User user, UUID contractId) {
        Contract contract = getActiveContract(contractId);

        if (!contract.getLessee().getId().equals(user.getId())) {
            return ApiResponse.error("Bu shartnoma sizga tegishli emas");
        }

        contract.setActive(false);
        contractRepository.save(contract);
        return ApiResponse.success("Shartnoma muvaffaqiyatli o‘chirildi");
    }

    @Override
    public ApiResponse<String> updateStatusContract(UUID contractId, boolean approved, User owner) {
        Contract contract = getActiveContract(contractId);

        if (!contract.getOwner().getId().equals(owner.getId())) {
            return ApiResponse.error("Bu shartnoma sizga tegishli emas");
        }

        if (approved) {
            if (contract.getProduct().getCount() <= 0) {
                return ApiResponse.error("Mahsulot mavjud emas");
            }
            contract.setContractStatus(ContractStatus.ACTIVE);
            Product product = contract.getProduct();
            product.setCount(product.getCount() - 1);
            productRepository.save(product);
        } else {
            contract.setContractStatus(ContractStatus.CANCELLED);
        }

        contractRepository.save(contract);

        String message = approved
                ? "Sizning shartnomangiz tasdiqlandi!"
                : "Kechirasiz, shartnomangiz rad etildi";

        notificationService.createNotification(
                contract.getLessee().getId(),
                ResNotification.builder()
                        .title("Shartnoma holati o‘zgardi")
                        .content(message + " Mahsulot: " + contract.getProduct().getName())
                        .build()
        );

        return ApiResponse.success("Shartnoma holati yangilandi");
    }

    @Override
    public ApiResponse<ResPageable> getAllContractsByUser(User user, String productName, ContractStatus status, int page, int size) {
        UUID userId = user.getRole() == UserRole.ADMIN ? null : user.getId();
        String statusName = status != null ? status.name() : null;

        Page<Contract> contracts = contractRepository.searchContract(
                userId, productName, statusName, PageRequest.of(page, size)
        );

        if (contracts.isEmpty()) {
            return ApiResponse.error("Shartnomalar topilmadi");
        }

        List<ResContract> list = contracts.stream()
                .map(this::toResContract)
                .toList();

        ResPageable res = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(contracts.getTotalPages())
                .totalElements(contracts.getTotalElements())
                .body(list)
                .build();

        return ApiResponse.success(res);
    }

    @Override
    public ApiResponse<ResContract> getContractById(UUID contractId) {
        Contract contract = getActiveContract(contractId);
        return ApiResponse.success(toResContractFull(contract));
    }

    @Scheduled(fixedRate = 3_600_000) // Har soatda
    public void finishExpiredContracts() {
        List<Contract> expired = contractRepository.findFinishedContracts(LocalDateTime.now());

        for (Contract c : expired) {
            c.setActive(false);
            contractRepository.save(c);

            String message = "Sizning \"" + c.getProduct().getName() + "\" uchun ijara shartnomasi muddati tugadi";

            notificationService.createNotification(c.getLessee().getId(),
                    ResNotification.builder().title("Ijara muddati tugadi").content(message).build());

            notificationService.createNotification(c.getOwner().getId(),
                    ResNotification.builder().title("Ijara muddati tugadi").content(message).build());
        }
    }

    // Helper methods
    private Product getActiveProduct(UUID id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Mahsulot topilmadi yoki mavjud emas"));
    }

    private Contract getActiveContract(UUID id) {
        return contractRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Shartnoma topilmadi"));
    }

    private double calculatePrice(LocalDateTime start, LocalDateTime end, UUID productId) {
        long hours = Duration.between(start, end).toHours();
        ProductPriceType type;
        long amount;

        if (hours < 24) {
            type = ProductPriceType.HOUR;
            amount = hours;
        } else if (hours < 720) {
            type = ProductPriceType.DAY;
            amount = Duration.between(start, end).toDays();
        } else if (hours < 8760) {
            type = ProductPriceType.MONTH;
            Period p = Period.between(start.toLocalDate(), end.toLocalDate());
            amount = p.getYears() * 12 + p.getMonths();
        } else {
            type = ProductPriceType.YEAR;
            amount = Period.between(start.toLocalDate(), end.toLocalDate()).getYears();
        }

        ProductPrice price = productPriceRepository.findByProductIdAndProductPriceType(productId, type)
                .orElseThrow(() -> new DataNotFoundException(type + " narxi topilmadi"));

        return price.getPrice() * amount;
    }

    private ResProductDuration getDuration(LocalDateTime start, LocalDateTime end) {
        long hours = Duration.between(start, end).toHours();
        if (hours < 24) return ResProductDuration.of(hours, ProductPriceType.HOUR);
        if (hours < 720) return ResProductDuration.of(Duration.between(start, end).toDays(), ProductPriceType.DAY);

        Period p = Period.between(start.toLocalDate(), end.toLocalDate());
        if (hours < 8760) return ResProductDuration.of(p.getYears() * 12 + p.getMonths(), ProductPriceType.MONTH);
        return ResProductDuration.of(p.getYears(), ProductPriceType.YEAR);
    }

    private ResContract toResContract(Contract c) {
        return ResContract.builder()
                .contractId(c.getId())
                .contractStatus(c.getContractStatus().name())
                .productName(c.getProduct().getName())
                .productId(c.getProduct().getId())
                .lesseeId(c.getLessee().getId())
                .lesseeName(c.getLessee().getUsername())
                .price(c.getPrice())
                .build();
    }

    private ResContract toResContractFull(Contract c) {
        ResContract res = toResContract(c);
        res.setStartDate(c.getStartDateTime());
        res.setEndDate(c.getEndDateTime());
        res.setDuration(getDuration(c.getStartDateTime(), c.getEndDateTime()));
        return res;
    }
}