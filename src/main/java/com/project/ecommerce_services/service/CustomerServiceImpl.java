package com.project.ecommerce_services.service;

import com.project.ecommerce_services.exceptions.APIException;
import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.model.Customer;
import com.project.ecommerce_services.model.User;
import com.project.ecommerce_services.model.enums.ERole;
import com.project.ecommerce_services.payload.customer.CustomerResponse;
import com.project.ecommerce_services.payload.customer.CustomerUpdateRequest;
import com.project.ecommerce_services.repository.CustomerRepository;
import com.project.ecommerce_services.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().getName().equals(ERole.ROLE_CUSTOMER))
                .map(user -> {
                    Customer customer = customerRepository.findById(user.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", user.getId()));
                    return new CustomerResponse(
                            user.getId(),
                            user.getEmail(),
                            customer.getFirstName(),
                            customer.getLastName(),
                            customer.getAddress(),
                            customer.getPhone(),
                            user.getRole().getName().name()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (!user.getRole().getName().equals(ERole.ROLE_CUSTOMER)) {
            throw new APIException("User is not a customer");
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return new CustomerResponse(
                user.getId(),
                user.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress(),
                customer.getPhone(),
                user.getRole().getName().name()
        );
    }

    @Override
    @Transactional
    public void updateCustomer(Long id, CustomerUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (!user.getRole().getName().equals(ERole.ROLE_CUSTOMER)) {
            throw new APIException("User is not a customer");
        }
        
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new APIException("Email is already taken!");
        }

        user.setEmail(request.getEmail());
        userRepository.save(user);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (!user.getRole().getName().equals(ERole.ROLE_CUSTOMER)) {
            throw new APIException("User is not a customer");
        }
        customerRepository.deleteById(id);
        userRepository.delete(user);
    }
}