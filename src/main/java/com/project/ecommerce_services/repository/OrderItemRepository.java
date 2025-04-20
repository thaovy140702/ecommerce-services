package com.project.ecommerce_services.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ecommerce_services.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
