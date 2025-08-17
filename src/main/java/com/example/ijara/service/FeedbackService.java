package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqFeedback;
import com.example.ijara.entity.User;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    ApiResponse<String> saveFeedback(User user, ReqFeedback reqFeedback);
    ApiResponse<String> updateFeedback(User user,UUID id, ReqFeedback reqFeedback);
    ApiResponse<String> deleteFeedback(User user,UUID id);
    ApiResponse<List<ReqFeedback>> getMyFeedback(User user);
}
