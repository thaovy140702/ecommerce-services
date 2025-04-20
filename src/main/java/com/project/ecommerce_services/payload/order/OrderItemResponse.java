package com.project.ecommerce_services.payload.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double subtotal;
}
