package com.project.ecommerce_services.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ecommerce_services.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
	Optional<Cart> findByUserId(Long userId);
}
