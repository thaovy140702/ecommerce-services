package com.project.ecommerce_services.payload.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double subtotal;
}
