package com.project.ecommerce_services.controller;

import com.project.ecommerce_services.payload.APIResponse;
import com.project.ecommerce_services.payload.CustomerResponse;
import com.project.ecommerce_services.payload.CustomerUpdateRequest;
import com.project.ecommerce_services.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomer(id, request);
        return ResponseEntity.ok(new APIResponse("Customer updated successfully!", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new APIResponse("Customer deleted successfully!", true));
    }
}