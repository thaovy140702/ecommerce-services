package com.project.ecommerce_services.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String role;
}