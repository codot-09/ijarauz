package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqFeedback;
import com.example.ijara.entity.User;
import com.example.ijara.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Tag(name = "Izohlar (Feedback)", description = "Foydalanuvchilar o‘z izohlarini boshqarishi")
@SecurityRequirement(name = "bearerAuth")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "Yangi izoh qoldirish")
    public ResponseEntity<ApiResponse<String>> createFeedback(
            @Valid @RequestBody ReqFeedback request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(feedbackService.saveFeedback(user, request));
    }

    @PutMapping("/{feedbackId}")
    @Operation(summary = "O‘z izohini tahrirlash")
    public ResponseEntity<ApiResponse<String>> updateFeedback(
            @Parameter(description = "Tahrirlanadigan izoh ID") 
            @PathVariable UUID feedbackId,
            @Valid @RequestBody ReqFeedback request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(feedbackService.updateFeedback(user, feedbackId, request));
    }

    @DeleteMapping("/{feedbackId}")
    @Operation(summary = "O‘z izohini o‘chirish")
    public ResponseEntity<ApiResponse<String>> deleteFeedback(
            @Parameter(description = "O‘chiriladigan izoh ID") 
            @PathVariable UUID feedbackId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(feedbackService.deleteFeedback(user, feedbackId));
    }

    @GetMapping("/my")
    @Operation(summary = "Men yozgan barcha izohlarni ko‘rish")
    public ResponseEntity<ApiResponse<List<ReqFeedback>>> getMyFeedbacks(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(feedbackService.getMyFeedback(user));
    }
}