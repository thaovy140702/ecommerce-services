package com.project.ecommerce_services.repository;

import com.project.ecommerce_services.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Optional<ProductReview> findByUserIdAndProductProductId(Long userId, Long productId);
    List<ProductReview> findByProductProductId(Long productId);
    
    @Query("SELECT AVG(r.rating) FROM product_reviews r WHERE r.product.productId = :productId")
    Double calculateAverageRating(Long productId);
}