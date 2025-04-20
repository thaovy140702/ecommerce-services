package com.project.ecommerce_services.payload.order;

import com.project.ecommerce_services.model.Order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private List<OrderItemResponse> orderItems;
}