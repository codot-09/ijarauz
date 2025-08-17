package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqFeedback;
import com.example.ijara.entity.User;
import com.example.ijara.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;


    @PostMapping
    @Operation(summary = "Feedback qushish uchun")
    public ResponseEntity<ApiResponse<String>> createFeedback(@RequestBody ReqFeedback reqFeedback,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedbackService.saveFeedback(user, reqFeedback));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Feedbackni update qilish uchun")
    public ResponseEntity<ApiResponse<String>> updateFeedback(@PathVariable UUID id,
                                                              @AuthenticationPrincipal User user,
                                                              @RequestBody ReqFeedback reqFeedback) {
        return ResponseEntity.ok(feedbackService.updateFeedback(user, id, reqFeedback));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Feedbackni o'chirish uchun")
    public ResponseEntity<ApiResponse<String>> deleteFeedback(@PathVariable UUID id,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedbackService.deleteFeedback(user, id));
    }


    @GetMapping
    @Operation(summary = "User o'zi yozgan feedbacklarni kurish")
    public ResponseEntity<ApiResponse<List<ReqFeedback>>> getFeedbacks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedbackService.getMyFeedback(user));
    }
 }
