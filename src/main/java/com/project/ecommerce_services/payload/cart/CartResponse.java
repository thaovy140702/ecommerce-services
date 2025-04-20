package com.project.ecommerce_services.payload.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> cartItems;
    private Double totalAmount;
}
