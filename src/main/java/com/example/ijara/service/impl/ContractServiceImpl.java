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
    private final NotificationServiceImpl notificationServiceImpl;

    @Override
    public ApiResponse<String> saveContract(User user, ReqContract reqContract) {
        Product product = productRepository.findByIdAndActiveTrue(reqContract.getProductId()).orElseThrow(
                () -> new DataNotFoundException("Product topilmadi")
        );

        Contract contract = Contract.builder()
                .owner(product.getOwner())
                .product(product)
                .price(getPrice(reqContract.getStartDate(), reqContract.getEndDate()))
                .contractStatus(ContractStatus.PENDING)
                .startDateTime(reqContract.getStartDate())
                .endDateTime(reqContract.getEndDate())
                .lessee(user)
                .active(true)
                .build();
        contractRepository.save(contract);

        notificationServiceImpl.createNotification(product.getOwner().getId(), ResNotification.builder()
                        .title("Siz uchun eslatma!")
                        .content(user.getUsername() + " " + "sizning " +
                                product.getName() + "nomli mahsulotingizni ijaraga olish uchun so'rov yubordi")
                .build());

        return ApiResponse.success("Success");
    }


    @Override
    public ApiResponse<String> updateContact(UUID contactId, User user, ReqContract reqContract) {
        Contract contract = contractRepository.findByIdAndActiveTrue(contactId).orElseThrow(
                () -> new DataNotFoundException("Contract not found")
        );

        if (!contract.getLessee().getId().equals(user.getId())) {
            return ApiResponse.error("Bu shartnoma sizga tegishli emas");
        }

        Product product = productRepository.findById(reqContract.getProductId()).orElseThrow(
                () -> new DataNotFoundException("Product topilmadi")
        );

        if (contract.getContractStatus() == ContractStatus.PENDING) {
            if (contract.getLessee().equals(user)) {
                contract.setStartDateTime(reqContract.getStartDate());
                contract.setEndDateTime(reqContract.getEndDate());
                contract.setProduct(product);
                contractRepository.save(contract);
                return ApiResponse.success("Success");
            }
        }

        return ApiResponse.error("Bu shartnoma sizniki emas");
    }



    @Override
    public ApiResponse<String> deleteContract(User user, UUID contractId) {
        Contract contract = contractRepository.findByIdAndActiveTrue(contractId).orElseThrow(
                () -> new DataNotFoundException("Contract topilmadi")
        );

        if (contract.getLessee().getId().equals(user.getId())) {
            contract.setActive(false);
            contractRepository.save(contract);
            return ApiResponse.success("Success");
        }

        return ApiResponse.error("Bu shartnoma sizniki emas");
    }


    @Override
    public ApiResponse<String> updateStatusContract(UUID contractId, boolean status, User user) {
        Contract contract = contractRepository.findByIdAndActiveTrue(contractId).orElseThrow(
                () -> new DataNotFoundException("Contract topilmadi")
        );

        if (contract.getOwner().getId().equals(user.getId())) {
            if (status){
                contract.setContractStatus(ContractStatus.ACTIVE);
                Product product = contract.getProduct();
                product.setCount(product.getCount() - 1);
                productRepository.save(product);
            } else {
                contract.setContractStatus(ContractStatus.CANCELLED);
            }
            contractRepository.save(contract);

            notificationServiceImpl.createNotification(contract.getLessee().getId(), ResNotification.builder()
                            .title("Siz uchun eslatma!")
                            .content(contract.getOwner().getUsername() + " " + "Siz " +
                                    contract.getProduct().getName() + " nomli mahsulot uchun yuborgan shartnomangiz tasqidlandi")
                    .build());

            return ApiResponse.success("Success");
        }
        return ApiResponse.error("Bu shartnoma siz uchun emas");
    }

    @Override
    public ApiResponse<ResPageable> getAllContractByUser(User user, String productName,
                                                         ContractStatus status, int page, int size) {
        Page<Contract> contracts;

        if (user.getRole().equals(UserRole.ADMIN)){
            contracts = contractRepository.searchContract(null,
                    productName, status != null ? status.name() : null, PageRequest.of(page, size));
        } else {
            contracts = contractRepository.searchContract(user.getId(),
                    productName, status != null ? status.name() : null, PageRequest.of(page, size));
        }

        if (contracts.getTotalElements() == 0) {
            return ApiResponse.error("Shartnomalar topilmadi");
        }

        List<ResContract> list = contracts.stream().map(this::resContract).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(contracts.getTotalPages())
                .totalElements(contracts.getTotalElements())
                .body(list)
                .build();
        return ApiResponse.success(resPageable);
    }

    @Override
    public ApiResponse<ResContract> getContractById(UUID contractId) {
        Contract contract = contractRepository.findByIdAndActiveTrue(contractId).orElseThrow(
                () -> new DataNotFoundException("Contract topilmadi")
        );

        ResContract resContract = ResContract.builder()
                .contractId(contract.getId())
                .contractStatus(contract.getContractStatus().name())
                .productName(contract.getProduct().getName())
                .productId(contract.getProduct().getId())
                .lesseeId(contract.getLessee().getId())
                .lesseeName(contract.getLessee().getUsername())
                .price(contract.getPrice())
                .startDate(contract.getStartDateTime())
                .endDate(contract.getEndDateTime())
                .duration(getDuration(contract.getStartDateTime(),contract.getEndDateTime()))
                .build();
        return ApiResponse.success(resContract);
    }

    // shartnomani muddati tugaganda avtomatik false qilib notification yuborish uchun
    @Scheduled(fixedRate = 3600000)
    public void findFinishedContracts(){
        List<Contract> finishedContracts = contractRepository.findFinishedContracts(LocalDateTime.now());
        for (Contract finishedContract : finishedContracts) {
            //ijaraga olgan odam uchun notification
            notificationServiceImpl.createNotification(finishedContract.getLessee().getId(), ResNotification.builder()
                            .title("Siz uchun eslatma!")
                            .content("Eslatib o'tamiz!!!  Sizning " +
                                    finishedContract.getProduct().getName() + " nomli mahsulot uchun yaratilgan shartnoma muggati tugadi")
                    .build());

            // ijarag bergan odam uchun notification
            notificationServiceImpl.createNotification(finishedContract.getOwner().getId(), ResNotification.builder()
                    .title("Siz uchun eslatma!")
                    .content("Eslatib o'tamiz!!!  Sizning " +
                            finishedContract.getProduct().getName() + " nomli mahsulot uchun yaratilgan shartnoma muggati tugadi")
                    .build());

            finishedContract.setActive(false);
            contractRepository.save(finishedContract);
        }
    }



    // contractni tuliq summasini hisoblash
    private double getPrice(LocalDateTime startDate, LocalDateTime endDate) {
        long hours = Duration.between(startDate, endDate).toHours();

        if (hours < 24) {
            // soatlik narx
            ProductPrice productPrice = productPriceRepository.findByProductPriceType(ProductPriceType.HOUR)
                    .orElseThrow(() -> new DataNotFoundException("Hour price not found"));
            return productPrice.getPrice() * hours;

        } else if (hours < 720) {
            // kunlik narx
            long days = Duration.between(startDate, endDate).toDays();
            ProductPrice productPrice = productPriceRepository.findByProductPriceType(ProductPriceType.DAY)
                    .orElseThrow(() -> new DataNotFoundException("Day price not found"));
            return productPrice.getPrice() * days;

        } else if (hours < 8760) { // 1 yildan kichik
            // oylik narx
            Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
            int months = period.getMonths() + period.getYears() * 12;
            ProductPrice productPrice = productPriceRepository.findByProductPriceType(ProductPriceType.MONTH)
                    .orElseThrow(() -> new DataNotFoundException("Month price not found"));
            return productPrice.getPrice() * months;

        } else {
            // yillik narx
            Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
            int years = period.getYears();
            ProductPrice productPrice = productPriceRepository.findByProductPriceType(ProductPriceType.YEAR)
                    .orElseThrow(() -> new DataNotFoundException("Year price not found"));
            return productPrice.getPrice() * years;
        }
    }


    private ResContract resContract(Contract contract){
        return ResContract.builder()
                .contractId(contract.getId())
                .contractStatus(contract.getContractStatus().name())
                .productName(contract.getProduct().getName())
                .productId(contract.getProduct().getId())
                .lesseeId(contract.getLessee().getId())
                .lesseeName(contract.getLessee().getUsername())
                .price(contract.getPrice())
                .build();
    }


    // contractni vaqtini olish
    private ResProductDuration getDuration(LocalDateTime startDate, LocalDateTime endDate) {
        long hours = Duration.between(startDate, endDate).toHours();

        if (hours < 24) {
            // soatlik duration
            return ResProductDuration.builder().duration(hours).productPriceType(ProductPriceType.HOUR).build();

        } else if (hours < 720) {
            // kunlik duration
            long days = Duration.between(startDate, endDate).toDays();
            return ResProductDuration.builder().duration(days).productPriceType(ProductPriceType.DAY).build();

        } else if (hours < 8760) { // 1 yildan kichik
            // oylik duration
            Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
            return ResProductDuration.builder().duration(period.getMonths()).productPriceType(ProductPriceType.MONTH).build() ;

        } else {
            // yillik duration
            Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
            return ResProductDuration.builder().duration(period.getYears()).productPriceType(ProductPriceType.YEAR).build() ;
        }
    }

}
