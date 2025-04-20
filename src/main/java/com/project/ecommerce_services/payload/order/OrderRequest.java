package com.project.ecommerce_services.payload.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotBlank(message = "Shipping address is required")
    @Size(max = 255, message = "Shipping address must not exceed 255 characters")
    private String shippingAddress;
}