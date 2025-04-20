package com.project.ecommerce_services.service;

import com.project.ecommerce_services.payload.cart.CartItemRequest;
import com.project.ecommerce_services.payload.cart.CartResponse;

public interface CartService {
    CartResponse addToCart(Long userId, CartItemRequest request);
    CartResponse getCart(Long userId);
    CartResponse updateCartItem(Long userId, Long cartItemId, Integer quantity);
    void removeFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}