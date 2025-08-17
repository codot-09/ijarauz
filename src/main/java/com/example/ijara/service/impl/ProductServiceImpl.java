package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.ProductDTO;
import com.example.ijara.dto.request.ReqProduct;
import com.example.ijara.dto.request.ReqProductPrice;
import com.example.ijara.dto.response.ResFeedback;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.dto.response.ResProduct;
import com.example.ijara.entity.Feedback;
import com.example.ijara.entity.Product;
import com.example.ijara.entity.ProductPrice;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ProductCondition;
import com.example.ijara.entity.enums.ProductPriceType;
import com.example.ijara.entity.enums.ProductType;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.repository.FeedbackRepository;
import com.example.ijara.repository.ProductPriceRepository;
import com.example.ijara.repository.ProductRepository;
import com.example.ijara.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public ApiResponse<String> addProduct(User user, ReqProduct reqProduct, ProductCondition productCondition, ProductType productType) {
        Product product = Product.builder()
                .name(reqProduct.getName())
                .description(reqProduct.getDescription())
                .lat(reqProduct.getLat())
                .lng(reqProduct.getLng())
                .imgUrls(reqProduct.getImgUrls())
                .owner(user)
                .productCondition(productCondition)
                .productType(productType)
                .build();
        Product save = productRepository.save(product);

        if (!reqProduct.getReqProductPrices().isEmpty()) {
            for (ReqProductPrice productPrice : reqProduct.getReqProductPrices()) {
                ProductPrice build = ProductPrice.builder()
                        .product(save)
                        .productPriceType(ProductPriceType.valueOf(productPrice.getProductPriceType()))
                        .price(productPrice.getPrice())
                        .build();
                productPriceRepository.save(build);
            }

        }

        return ApiResponse.success("Product added successfully");

    }

    @Override
    public ApiResponse<String> updateProduct(UUID id, User user, ReqProduct reqProduct, ProductCondition productCondition, ProductType productType) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Product not found")
        );

        if (!product.getOwner().equals(user)) {
            return ApiResponse.error("Bu sizning mahsulotingiz emas");
        }

        product.setName(reqProduct.getName());
        product.setDescription(reqProduct.getDescription());
        product.setLat(reqProduct.getLat());
        product.setLng(reqProduct.getLng());
        product.setImgUrls(reqProduct.getImgUrls());
        product.setOwner(user);
        product.setProductCondition(productCondition);
        product.setProductType(productType);

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

        productPriceRepository.deleteAll(allByProductId);

        productRepository.delete(product);

        return ApiResponse.success("Product deleted successfully");
    }



    @Override
    public ApiResponse<ResPageable> getAllProduct(String name, ProductType productType, int page, int size) {
        // productType null yuborishi uchun
        String type = productType != null ? productType.name() : null;

        Page<ResProduct> resProducts = productRepository.searchProduct(name, type, PageRequest.of(page, size));

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(resProducts.getTotalElements())
                .totalPage(resProducts.getTotalPages())
                .body(resProducts.getContent())
                .build();
        return ApiResponse.success(resPageable);
    }



    @Override
    public ApiResponse<ProductDTO> getProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
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
                .productCondition(product.getProductCondition().name())
                .productType(product.getProductType().name())
                .rating(averageRating(feedbackRepository.findAllByProductId(product.getId())))
                .reqProductPrices(reqProductPrices)
                .build();
        return ApiResponse.success(productDTO);
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
