package com.project.ecommerce_services.service;

import com.project.ecommerce_services.exceptions.APIException;
import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.model.Cart;
import com.project.ecommerce_services.model.CartItem;
import com.project.ecommerce_services.model.Product;
import com.project.ecommerce_services.model.User;
import com.project.ecommerce_services.payload.cart.CartItemRequest;
import com.project.ecommerce_services.payload.cart.CartItemResponse;
import com.project.ecommerce_services.payload.cart.CartResponse;
import com.project.ecommerce_services.repository.CartItemRepository;
import com.project.ecommerce_services.repository.CartRepository;
import com.project.ecommerce_services.repository.ProductRepository;
import com.project.ecommerce_services.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
                           UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (product.getQuantity() < request.getQuantity()) {
            throw new APIException("Insufficient product quantity");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setPrice(product.getSpecialPrice() > 0 ? product.getSpecialPrice() : product.getPrice());
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getSpecialPrice() > 0 ? product.getSpecialPrice() : product.getPrice());
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        return getCart(userId);
    }

    @Override
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user id: ", userId));

        List<CartItemResponse> cartItems = cart.getCartItems().stream()
                .map(item -> new CartItemResponse(
                        item.getCartItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() * item.getQuantity()
                ))
                .collect(Collectors.toList());

        Double totalAmount = cartItems.stream()
                .mapToDouble(CartItemResponse::getSubtotal)
                .sum();

        return new CartResponse(cart.getCartId(), userId, cartItems, totalAmount);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", cartItemId));

        Product product = cartItem.getProduct();
        if (product.getQuantity() < quantity) {
            throw new APIException("Insufficient product quantity");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","user id", userId));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", cartItemId));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","user id: " + userId));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
