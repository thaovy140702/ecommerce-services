package com.project.ecommerce_services.service;

import java.util.List;

import com.project.ecommerce_services.model.Order.OrderStatus;
import com.project.ecommerce_services.payload.order.OrderRequest;
import com.project.ecommerce_services.payload.order.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(Long userId, OrderRequest request);
    OrderResponse getOrderById(Long userId, Long orderId);
    List<OrderResponse> getAllOrders(Long userId);
    List<OrderResponse> getAllOrdersForAdmin();
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
	OrderResponse cancelOrder(Long userId, Long orderId);
}
