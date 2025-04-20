package com.project.ecommerce_services.service;

import com.project.ecommerce_services.exceptions.APIException;
import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.model.*;
import com.project.ecommerce_services.payload.review.ProductReviewRequest;
import com.project.ecommerce_services.payload.review.ProductReviewResponse;
import com.project.ecommerce_services.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository,
                                    UserRepository userRepository,
                                    ProductRepository productRepository,
                                    OrderRepository orderRepository,
                                    CustomerRepository customerRepository) {
        this.productReviewRepository = productReviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public ProductReviewResponse createReview(Long userId, ProductReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        boolean hasPurchased = orderRepository.findByUserId(userId).stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().getProductId().equals(request.getProductId()));
        if (!hasPurchased) {
            throw new APIException("You can only review products you have purchased and received");
        }

        if (productReviewRepository.findByUserIdAndProductProductId(userId, request.getProductId()).isPresent()) {
            throw new APIException("You have already reviewed this product");
        }

        ProductReview review = new ProductReview();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        productReviewRepository.save(review);

        updateAverageRating(product);

        return mapToReviewResponse(review);
    }

    @Override
    @Transactional
    public ProductReviewResponse updateReview(Long userId, Long reviewId, ProductReviewRequest request) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id" + reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new APIException("You can only update your own reviews");
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id" + request.getProductId()));
        if (!review.getProduct().getProductId().equals(request.getProductId())) {
            throw new APIException("Cannot change the product of an existing review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        productReviewRepository.save(review);

        updateAverageRating(product);

        return mapToReviewResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id" + reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new APIException("You can only delete your own reviews");
        }

        Product product = review.getProduct();
        productReviewRepository.delete(review);

        updateAverageRating(product);
    }

    @Override
    public List<ProductReviewResponse> getReviewsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id" + productId);
        }
        return productReviewRepository.findByProductProductId(productId).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private void updateAverageRating(Product product) {
        Double avgRating = productReviewRepository.calculateAverageRating(product.getProductId());
        product.setAverageRating(avgRating != null ? avgRating : 0.0);
        productRepository.save(product);
    }

    private ProductReviewResponse mapToReviewResponse(ProductReview review) {
        Customer customer = customerRepository.findById(review.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", review.getUser().getId()));
        String userName = customer.getFirstName() + " " + customer.getLastName();
        return new ProductReviewResponse(
                review.getReviewId(),
                review.getUser().getId(),
                userName,
                review.getProduct().getProductId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}