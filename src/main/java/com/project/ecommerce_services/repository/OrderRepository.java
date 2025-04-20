package com.project.ecommerce_services.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ecommerce_services.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
	List<Order> findByUserId(Long userId);
}
