package com.project.ecommerce_services.service;

import java.util.List;

import com.project.ecommerce_services.payload.review.ProductReviewRequest;
import com.project.ecommerce_services.payload.review.ProductReviewResponse;

public interface ProductReviewService {
    ProductReviewResponse createReview(Long userId, ProductReviewRequest request);
    ProductReviewResponse updateReview(Long userId, Long reviewId, ProductReviewRequest request);
    void deleteReview(Long userId, Long reviewId);
    List<ProductReviewResponse> getReviewsByProduct(Long productId);
}