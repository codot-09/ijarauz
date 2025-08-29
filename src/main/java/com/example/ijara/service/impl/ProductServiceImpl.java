package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.ProductDTO;
import com.example.ijara.dto.request.ReqProduct;
import com.example.ijara.dto.request.ReqProductPrice;
import com.example.ijara.dto.response.ResFeedback;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.dto.response.ResProduct;
import com.example.ijara.entity.*;
import com.example.ijara.entity.enums.ProductCondition;
import com.example.ijara.entity.enums.ProductPriceType;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.repository.CategoryRepository;
import com.example.ijara.repository.FeedbackRepository;
import com.example.ijara.repository.ProductPriceRepository;
import com.example.ijara.repository.ProductRepository;
import com.example.ijara.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final FeedbackRepository feedbackRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ApiResponse<String> addProduct(User user, ReqProduct reqProduct, ProductCondition productCondition) {

        Category category = categoryRepository.findById(reqProduct.getCategoryId()).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        Product product = Product.builder()
                .name(reqProduct.getName())
                .description(reqProduct.getDescription())
                .lat(reqProduct.getLat())
                .lng(reqProduct.getLng())
                .imgUrls(reqProduct.getImgUrls())
                .owner(user)
                .productCondition(productCondition)
                .category(category)
                .count(reqProduct.getCount())
                .active(true)
                .build();
        Product save = productRepository.save(product);

        if (!reqProduct.getReqProductPrices().isEmpty()) {
            for (ReqProductPrice productPrice : reqProduct.getReqProductPrices()) {
                ProductPrice build = ProductPrice.builder()
                        .product(save)
                        .productPriceType(ProductPriceType.valueOf(productPrice.getProductPriceType()))
                        .price(productPrice.getPrice())
                        .active(true)
                        .build();
                productPriceRepository.save(build);
            }

        }

        return ApiResponse.success("Product added successfully");

    }

    @Override
    public ApiResponse<String> updateProduct(UUID id, User user, ReqProduct reqProduct, ProductCondition productCondition) {
        Product product = productRepository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new DataNotFoundException("Product not found")
        );

        if (!product.getOwner().getId().equals(user.getId())) {
            return ApiResponse.error("Bu sizning mahsulotingiz emas");
        }

        Category category = categoryRepository.findById(reqProduct.getCategoryId()).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        product.setName(reqProduct.getName());
        product.setDescription(reqProduct.getDescription());
        product.setLat(reqProduct.getLat());
        product.setLng(reqProduct.getLng());
        product.setImgUrls(reqProduct.getImgUrls());
        product.setOwner(user);
        product.setCount(reqProduct.getCount());
        product.setProductCondition(productCondition);
        product.setCategory(category);

        Product save = productRepository.save(product);

        if (!reqProduct.getReqProductPrices().isEmpty()) {
            //Eski ruyhatni uchirib yangi yaratiladi
            List<ProductPrice> allByProductId = productPriceRepository.findAllByProductId(id);
            productPriceRepository.deleteAll(allByProductId);

            for (ReqProductPrice productPrice : reqProduct.getReqProductPrices()) {
                ProductPrice build = ProductPrice.builder()
                        .product(save)
                        .productPriceType(ProductPriceType.valueOf(productPrice.getProductPriceType()))
                        .price(productPrice.getPrice())
                        .build();
                productPriceRepository.save(build);
            }

        }

        return ApiResponse.success("Product updated successfully");
    }


    @Override
    public ApiResponse<String> deleteProduct(UUID id, User user) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Product topiladi")
        );

        if (!product.getOwner().equals(user)) {
            return ApiResponse.error("Bu sizning mahsulotingiz emas");
        }

        List<ProductPrice> allByProductId = productPriceRepository.findAllByProductId(id);

        for (ProductPrice productPrice : allByProductId) {
            productPrice.setActive(false);
            productPriceRepository.save(productPrice);
        }

        product.setActive(false);
        productRepository.save(product);

        return ApiResponse.success("Product deleted successfully");
    }



    @Override
    public ApiResponse<ResPageable> getAllProduct(String name, String categoryName, boolean active, int page, int size) {
        // productType null yuborishi uchun

        Page<Product> product = productRepository.searchProduct(name, categoryName, true, PageRequest.of(page, size));
        if (product.getTotalElements() == 0){
            return ApiResponse.error("Product topilmadi");
        }

        List<ResProduct> resProducts = new ArrayList<>();

        for (Product product1 : product.getContent()) {
            ProductPrice price = productPriceRepository.findByProductIdAndProductPriceType(product1.getId(), ProductPriceType.DAY);
            ResProduct productDTO = ResProduct.builder()
                    .id(product1.getId())
                    .name(product1.getName())
                    .description(product1.getDescription())
                    .imgUrls(product1.getImgUrls())
                    .lat(product1.getLat())
                    .lng(product1.getLng())
                    .rating(averageRating(feedbackRepository.findAllByProductId(product1.getId())))
                    .productCondition(product1.getProductCondition().name())
                    .productType(product1.getCategory().getName())
                    .price(price.getPrice())
                    .build();
            resProducts.add(productDTO);
        }


        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(product.getTotalElements())
                .totalPage(product.getTotalPages())
                .body(resProducts)
                .build();
        return ApiResponse.success(resPageable);
    }



    @Override
    public ApiResponse<ProductDTO> getProduct(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new DataNotFoundException("Product topilmadi")
        );

        List<ResFeedback> resFeedbacks = convertFeedbackInDTO(feedbackRepository.findAllByProductId(product.getId()));
        List<ReqProductPrice> reqProductPrices = convertPriceInDTO(productPriceRepository.findAllByProductId(product.getId()));

        ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .lat(product.getLat())
                .lng(product.getLng())
                .imgUrls(product.getImgUrls())
                .feedbackList(resFeedbacks)
                .count(product.getCount())
                .productCondition(product.getProductCondition().name())
                .productType(product.getCategory().getName())
                .rating(averageRating(feedbackRepository.findAllByProductId(product.getId())))
                .reqProductPrices(reqProductPrices)
                .build();
        return ApiResponse.success(productDTO);
    }


    @Scheduled(fixedRate = 3600000)
    private void unActiveProduct(){
        // hozirgi vaqtni olish
        LocalDateTime localDateTime = LocalDateTime.now();

        // barcha productlarni topib bittasini olish
        for (Product product : productRepository.findAll()) {

            // o'rtadagi vaqtni aniqlash, necha soat bulganini
            long hour = Duration.between(product.getCreatedAt(), localDateTime).toHours();

            //user roli tekshirildi
            if (!product.getOwner().getRole().equals(UserRole.COMPANY)){
                //agar 48 soatdan katta yoki teng bulsa active = false
                if (hour >= 48){
                    product.setActive(false);
                    productRepository.save(product);
                }
            }
        }
    }



    private List<ResFeedback> convertFeedbackInDTO(List<Feedback> feedbackList){
        if (feedbackList.isEmpty()){
            return new ArrayList<>();
        }

        return feedbackList.stream().map(
                feedback -> ResFeedback.builder()
                        .feedback(feedback.getFeedback())
                        .rating(feedback.getRating())
                        .userId(feedback.getUser().getId())
                        .userName(feedback.getUser().getFirstName() + " " + feedback.getUser().getLastName())
                        .build()
        ).toList();
    }



    private double averageRating(List<Feedback> feedbackList){
        if (feedbackList.isEmpty()){
            return 0.0;
        }
        double sum = 0;
        for (Feedback feedback : feedbackList) {
            sum += feedback.getRating();
        }

        return sum / feedbackList.size();
    }


    private List<ReqProductPrice> convertPriceInDTO(List<ProductPrice> productPriceList){
        return productPriceList.stream().map(
                productPrice -> ReqProductPrice.builder()
                        .price(productPrice.getPrice())
                        .productPriceType(productPrice.getProductPriceType().name())
                        .build()
        ).toList();
    }

}
