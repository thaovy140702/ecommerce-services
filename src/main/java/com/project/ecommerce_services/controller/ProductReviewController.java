package com.project.ecommerce_services.controller;

import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.payload.APIResponse;
import com.project.ecommerce_services.payload.review.ProductReviewRequest;
import com.project.ecommerce_services.payload.review.ProductReviewResponse;
import com.project.ecommerce_services.repository.UserRepository;
import com.project.ecommerce_services.service.ProductReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;
    private final UserRepository userRepository;

    public ProductReviewController(ProductReviewService productReviewService, UserRepository userRepository) {
        this.productReviewService = productReviewService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ProductReviewResponse> createReview(@Valid @RequestBody ProductReviewRequest request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(productReviewService.createReview(userId, request));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ProductReviewResponse> updateReview(@PathVariable Long reviewId,
                                                             @Valid @RequestBody ProductReviewRequest request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(productReviewService.updateReview(userId, reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<APIResponse> deleteReview(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        productReviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(new APIResponse("Review deleted successfully!", true));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productReviewService.getReviewsByProduct(productId));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email" + email))
                .getId();
    }
}