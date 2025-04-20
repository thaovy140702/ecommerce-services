package com.project.ecommerce_services.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ecommerce_services.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

}
