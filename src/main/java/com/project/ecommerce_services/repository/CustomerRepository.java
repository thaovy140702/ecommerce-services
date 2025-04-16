package com.project.ecommerce_services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.ecommerce_services.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}