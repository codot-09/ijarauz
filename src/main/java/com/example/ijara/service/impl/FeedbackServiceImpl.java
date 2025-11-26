package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqFeedback;
import com.example.ijara.entity.Feedback;
import com.example.ijara.entity.Product;
import com.example.ijara.entity.User;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.repository.FeedbackRepository;
import com.example.ijara.repository.ProductRepository;
import com.example.ijara.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ProductRepository productRepository;

    @Override
    public ApiResponse<String> saveFeedback(User user, ReqFeedback req) {
        Product product = getProductById(req.getProductId());

        Feedback feedback = Feedback.builder()
                .user(user)
                .product(product)
                .rating(req.getRating())
                .feedback(req.getFeedback())
                .build();

        feedbackRepository.save(feedback);
        return ApiResponse.success("Izoh muvaffaqiyatli qoldirildi");
    }

    @Override
    public ApiResponse<String> updateFeedback(User user, UUID feedbackId, ReqFeedback req) {
        Feedback feedback = getFeedbackByIdAndUser(feedbackId, user.getId());

        Product product = getProductById(req.getProductId());

        feedback.setProduct(product);
        feedback.setRating(req.getRating());
        feedback.setFeedback(req.getFeedback());

        feedbackRepository.save(feedback);
        return ApiResponse.success("Izoh muvaffaqiyatli yangilandi");
    }

    @Override
    public ApiResponse<String> deleteFeedback(User user, UUID feedbackId) {
        Feedback feedback = getFeedbackByIdAndUser(feedbackId, user.getId());
        feedbackRepository.delete(feedback);
        return ApiResponse.success("Izoh muvaffaqiyatli o‘chirildi");
    }

    @Override
    public ApiResponse<List<ReqFeedback>> getMyFeedback(User user) {
        List<ReqFeedback> myFeedbacks = feedbackRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toReqFeedback)
                .toList();

        return myFeedbacks.isEmpty()
                ? ApiResponse.error("Siz hali hech qanday izoh qoldirmagansiz")
                : ApiResponse.success(myFeedbacks);
    }

    // Helper methods
    private Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Mahsulot topilmadi"));
    }

    private Feedback getFeedbackByIdAndUser(UUID feedbackId, UUID userId) {
        return feedbackRepository.findByIdAndUserId(feedbackId, userId)
                .orElseThrow(() -> new DataNotFoundException("Izoh topilmadi yoki sizga tegishli emas"));
    }

    private ReqFeedback toReqFeedback(Feedback f) {
        return ReqFeedback.builder()
                .productId(f.getProduct().getId())
                .productName(f.getProduct().getName())
                .rating(f.getRating())
                .feedback(f.getFeedback())
                .build();
    }
}