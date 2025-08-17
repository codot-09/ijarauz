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


    private final ProductRepository productRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public ApiResponse<String> saveFeedback(User user, ReqFeedback reqFeedback) {
        Product product = productRepository.findById(reqFeedback.getProductId()).orElseThrow(
                () -> new DataNotFoundException("Product topilmadi")
        );

        Feedback feedback = Feedback.builder()
                .feedback(reqFeedback.getFeedback())
                .rating(reqFeedback.getRating())
                .user(user)
                .product(product)
                .build();
        feedbackRepository.save(feedback);
        return ApiResponse.success("Feedback saqlandi");
    }

    @Override
    public ApiResponse<String> updateFeedback(User user, UUID id, ReqFeedback reqFeedback) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Feedback topilmadi")
        );

        if (!feedback.getUser().equals(user)){
            return ApiResponse.error("Bu izoh sizniki emas");
        }

        Product product = productRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Product topilmadi")
        );

        feedback.setRating(reqFeedback.getRating());
        feedback.setProduct(product);
        feedback.setFeedback(reqFeedback.getFeedback());
        feedbackRepository.save(feedback);
        return ApiResponse.success("Feedback tahrirlandi");
    }

    @Override
    public ApiResponse<String> deleteFeedback(User user, UUID id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Feedback topilmadi")
        );

        if (!feedback.getUser().equals(user)){
            return ApiResponse.error("Bu izoh sizniki emas");
        }

        feedbackRepository.delete(feedback);
        return ApiResponse.success("Feedback o'chirildi");
    }

    @Override
    public ApiResponse<List<ReqFeedback>> getMyFeedback(User user) {
        List<ReqFeedback> list = feedbackRepository.findAllByUserId(user.getId()).stream().map(this::convertFeedback).toList();
        return ApiResponse.success(list);
    }


    private ReqFeedback convertFeedback(Feedback feedback) {
        return ReqFeedback.builder()
                .feedback(feedback.getFeedback())
                .rating(feedback.getRating())
                .productId(feedback.getProduct().getId())
                .productName(feedback.getProduct().getName())
                .build();
    }
}
