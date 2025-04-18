package com.project.ecommerce_services.service;

import java.util.List;

import com.project.ecommerce_services.payload.CustomerResponse;
import com.project.ecommerce_services.payload.CustomerUpdateRequest;

public interface CustomerService {
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(Long id);
    void updateCustomer(Long id, CustomerUpdateRequest request);
    void deleteCustomer(Long id);
}