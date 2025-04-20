package com.project.ecommerce_services.payload.review;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponse {
	 private Long reviewId;
	    private Long userId;
	    private String userName;
	    private Long productId;
	    private Integer rating;
	    private String comment;
	    private LocalDateTime createdAt;
}