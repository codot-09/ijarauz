package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.ProductDTO;
import com.example.ijara.dto.request.ReqProduct;
import com.example.ijara.dto.request.ReqProductPrice;
import com.example.ijara.dto.response.ProductMapView;
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
import com.example.ijara.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    @Transactional
    public ApiResponse<String> addProduct(User owner, ReqProduct req, ProductCondition condition) {
        Category category = getCategoryById(req.getCategoryId());

        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .lat(req.getLat())
                .lng(req.getLng())
                .imgUrls(req.getImgUrls())
                .owner(owner)
                .category(category)
                .productCondition(condition)
                .count(req.getCount())
                .active(true)
                .build();

        product = productRepository.save(product);
        saveProductPrices(product, req.getReqProductPrices());

        return ApiResponse.success("Mahsulot muvaffaqiyatli qo‘shildi");
    }

    @Override
    @Transactional
    public ApiResponse<String> updateProduct(UUID productId, User user, ReqProduct req, ProductCondition condition) {
        Product product = getActiveProductByIdAndOwner(productId, user.getId());
        Category category = getCategoryById(req.getCategoryId());

        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setLat(req.getLat());
        product.setLng(req.getLng());
        product.setImgUrls(req.getImgUrls());
        product.setCount(req.getCount());
        product.setProductCondition(condition);
        product.setCategory(category);

        productRepository.save(product);
        productPriceRepository.deleteByProductId(productId);
        saveProductPrices(product, req.getReqProductPrices());

        return ApiResponse.success("Mahsulot muvaffaqiyatli yangilandi");
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteProduct(UUID productId, User user) {
        Product product = getProductByIdAndOwner(productId, user.getId());

        product.setActive(false);
        productRepository.save(product);

        productPriceRepository.deactivateByProductId(productId);
        return ApiResponse.success("Mahsulot muvaffaqiyatli o‘chirildi");
    }

    @Override
    public ApiResponse<ResPageable> getAllProducts(String name, UUID categoryId, int page, int size) {
        Page<Product> products = productRepository.findAllByActiveTrue(
                ProductSpecification.filter(name, categoryId),
                PageRequest.of(page, size)
        );

        if (products.isEmpty()) {
            return ApiResponse.error("Mahsulotlar topilmadi");
        }

        List<ResProduct> list = products.stream()
                .map(this::toResProduct)
                .toList();

        ResPageable response = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(products.getTotalElements())
                .totalPage(products.getTotalPages())
                .body(list)
                .build();

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<ProductDTO> getProductById(UUID productId) {
        Product product = getActiveProductById(productId);

        List<Feedback> feedbackEntities = feedbackRepository.findAllByProductId(productId);

        List<ResFeedback> feedbacks = feedbackEntities.stream()
                .map(this::toResFeedback)
                .toList();

        List<ReqProductPrice> prices = productPriceRepository.findAllByProductId(productId)
                .stream()
                .map(this::toReqProductPrice)
                .toList();

        double avgRating = calculateAverageRating(feedbackEntities);

        ProductDTO dto = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .lat(product.getLat())
                .lng(product.getLng())
                .imgUrls(product.getImgUrls())
                .count(product.getCount())
                .productCondition(product.getProductCondition().name())
                .productType(product.getCategory().getName())
                .rating(avgRating)
                .feedbackList(feedbacks)
                .reqProductPrices(prices)
                .build();

        return ApiResponse.success(dto);
    }

    @Override
    public ApiResponse<List<ResProduct>> getMyProducts(User user) {
        List<Product> products = productRepository.findByOwner(user);

        List<ResProduct> list = products.stream()
                .map(this::toResProduct)
                .toList();

        return ApiResponse.success(list);

    }

    @Override
    public ApiResponse<ResPageable> getTopRatedProducts(int page,int size) {
        Page<Product> products = productRepository.findAll(PageRequest.of(page,size));

        List<ResProduct> rp = products.stream()
                .map(this::toResProduct)
                .sorted(Comparator.comparing(ResProduct::getRating).reversed()) // rating DESC
                .toList();

        ResPageable response = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(products.getTotalElements())
                .totalPage(products.getTotalPages())
                .body(rp)
                .build();

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<List<ProductMapView>> getNearbyProducts(double lat, double lng) {
        List<Product> products = productRepository.findNearbyProducts(lat,lng,10,10);

        List<ProductMapView> response = products.stream()
                .map(this::toView)
                .toList();

        return ApiResponse.success(response);
    }

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void deactivateOldProducts() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusHours(48);

        productRepository.deactivateProductsOlderThanAndNotCompany(twoDaysAgo, UserRole.COMPANY);
    }

    // Helper methods
    private Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Kategoriya topilmadi"));
    }

    private Product getActiveProductById(UUID id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Mahsulot topilmadi yoki faol emas"));
    }

    private Product getProductByIdAndOwner(UUID id, UUID ownerId) {
        return productRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new DataNotFoundException("Mahsulot topilmadi yoki sizga tegishli emas"));
    }

    private Product getActiveProductByIdAndOwner(UUID id, UUID ownerId) {
        return productRepository.findByIdAndOwnerIdAndActiveTrue(id, ownerId)
                .orElseThrow(() -> new DataNotFoundException("Mahsulot topilmadi yoki sizga tegishli emas"));
    }

    private void saveProductPrices(Product product, List<ReqProductPrice> prices) {
        if (prices == null || prices.isEmpty()) return;

        List<ProductPrice> productPrices = prices.stream()
                .map(p -> ProductPrice.builder()
                        .product(product)
                        .productPriceType(ProductPriceType.valueOf(p.getProductPriceType()))
                        .price(p.getPrice())
                        .active(true)
                        .build())
                .toList();

        productPriceRepository.saveAll(productPrices);
    }

    private ResProduct toResProduct(Product p) {
        Double dailyPrice = productPriceRepository.findPriceByProductIdAndType(p.getId(), ProductPriceType.DAY);
        double rating = calculateAverageRating(feedbackRepository.findAllByProductId(p.getId()));

        return ResProduct.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .imgUrls(p.getImgUrls())
                .lat(p.getLat())
                .lng(p.getLng())
                .productType(p.getCategory().getName())
                .productCondition(p.getProductCondition().name())
                .price(dailyPrice != null ? dailyPrice : 0.0)
                .rating(rating)
                .build();
    }

    private ResFeedback toResFeedback(Feedback f) {
        return ResFeedback.builder()
                .feedback(f.getFeedback())
                .rating(f.getRating())
                .userId(f.getUser().getId())
                .userName(f.getUser().getFirstName() + " " + f.getUser().getLastName())
                .build();
    }

    private ProductMapView toView(Product product){
        List<Feedback> f = feedbackRepository.findAllByProductId(product.getId());

        return new ProductMapView(
                product.getId(),
                product.getLat(),
                product.getLng(),
                product.getImgUrls().getFirst(),
                product.getName(),
                calculateAverageRating(f)
        );
    }

    private ReqProductPrice toReqProductPrice(ProductPrice pp) {
        return ReqProductPrice.builder()
                .price(pp.getPrice())
                .productPriceType(pp.getProductPriceType().name())
                .build();
    }

    private double calculateAverageRating(List<Feedback> feedbacks) {
        return feedbacks.isEmpty() ? 0.0 :
                feedbacks.stream().mapToDouble(Feedback::getRating).average().orElse(0.0);
    }
}