package com.project.ecommerce_services.service;

import com.project.ecommerce_services.exceptions.APIException;
import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.model.Cart;
import com.project.ecommerce_services.model.Order;
import com.project.ecommerce_services.model.OrderItem;
import com.project.ecommerce_services.model.Product;
import com.project.ecommerce_services.model.User;
import com.project.ecommerce_services.payload.order.OrderItemResponse;
import com.project.ecommerce_services.payload.order.OrderRequest;
import com.project.ecommerce_services.payload.order.OrderResponse;
import com.project.ecommerce_services.repository.CartRepository;
import com.project.ecommerce_services.repository.OrderRepository;
import com.project.ecommerce_services.repository.ProductRepository;
import com.project.ecommerce_services.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
//    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository, UserRepository userRepository,
                            ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
//        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user id", userId));

        if (cart.getCartItems().isEmpty()) {
            throw new APIException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    if (product.getQuantity() < cartItem.getQuantity()) {
                        throw new APIException("Insufficient quantity for product: " + product.getProductName());
                    }
                    product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                    productRepository.save(product);

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setTotalAmount(orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum());

        orderRepository.save(order);
        cartService.clearCart(userId);

        return mapToOrderResponse(order);
    }

    @Override
    public OrderResponse getOrderById(Long userId, Long orderId) {
     
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id" + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new APIException("Order does not belong to user");
        }

        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders(Long userId) {
    	if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> orderItems = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getOrderItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() * item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getOrderId(),
                order.getUser().getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                orderItems
        );
    }
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        switch (order.getStatus()) {
            case PENDING:
                if (status != Order.OrderStatus.CONFIRMED && status != Order.OrderStatus.CANCELLED) {
                    throw new APIException("Invalid status transition from PENDING to " + status);
                }
                break;
            case CONFIRMED:
                if (status != Order.OrderStatus.SHIPPED && status != Order.OrderStatus.CANCELLED) {
                    throw new APIException("Invalid status transition from CONFIRMED to " + status);
                }
                break;
            case SHIPPED:
                if (status != Order.OrderStatus.DELIVERED) {
                    throw new APIException("Invalid status transition from SHIPPED to " + status);
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new APIException("Cannot update status of " + order.getStatus() + " order");
            default:
                throw new APIException("Invalid order status");
        }

        if (status == Order.OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        orderRepository.save(order);

        return mapToOrderResponse(order);
    }
    
    
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id" + orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new APIException("Order does not belong to user");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new APIException("Cannot cancel order in " + order.getStatus() + " status");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        return mapToOrderResponse(order);
    }
    
}