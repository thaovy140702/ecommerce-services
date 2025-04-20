package com.project.ecommerce_services.controller;

import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.payload.APIResponse;
import com.project.ecommerce_services.payload.cart.CartItemRequest;
import com.project.ecommerce_services.payload.cart.CartResponse;
import com.project.ecommerce_services.repository.UserRepository;
import com.project.ecommerce_services.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long cartItemId,
                                                      @RequestBody Integer quantity) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.updateCartItem(userId, cartItemId, quantity));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<APIResponse> removeFromCart(@PathVariable Long cartItemId) {
        Long userId = getCurrentUserId();
        cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(new APIResponse("Item removed from cart successfully!", true));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<APIResponse> clearCart() {
        Long userId = getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(new APIResponse("Cart cleared successfully!", true));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();
    }
}