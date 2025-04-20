package com.project.ecommerce_services.controller;

import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.model.Order.OrderStatus;
import com.project.ecommerce_services.payload.order.OrderRequest;
import com.project.ecommerce_services.payload.order.OrderResponse;
import com.project.ecommerce_services.repository.UserRepository;
import com.project.ecommerce_services.service.OrderService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getOrderById(userId, orderId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getAllOrders(userId));
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrdersForAdmin() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }
    
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                          @RequestBody OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email: ", email))
                .getId();
    }
}