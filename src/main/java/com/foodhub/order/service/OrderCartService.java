package com.foodhub.order.service;

import com.foodhub.order.client.RestaurantClient;
import com.foodhub.order.entity.CartItem;
import com.foodhub.order.entity.Order;
import com.foodhub.order.exception.CartItemNotFoundException;
import com.foodhub.order.exception.EmptyCartException;
import com.foodhub.order.exception.OrderNotFoundException;
import com.foodhub.order.repository.CartItemRepository;
import com.foodhub.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCartService {
    private static final Logger logger = LoggerFactory.getLogger(OrderCartService.class);
    
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;

    public CartItem addToCart(CartItem cartItem) {
        List<CartItem> existingCart = cartItemRepository.findByUserIdAndOrderIdIsNull(cartItem.getUserId());
        if (!existingCart.isEmpty() && !existingCart.get(0).getRestaurantId().equals(cartItem.getRestaurantId())) {
            throw new RuntimeException("Cannot add items from different restaurants");
        }
        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartByUserId(Long userId) {
        return cartItemRepository.findByUserIdAndOrderIdIsNull(userId);
    }

    public CartItem updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserIdAndOrderIdIsNull(userId);
    }

    @Transactional
    public Order placeOrder(Long userId, Long addressId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndOrderIdIsNull(userId);
        if (cartItems.isEmpty()) {
            throw new EmptyCartException();
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setRestaurantId(cartItems.get(0).getRestaurantId());
        order.setStatus("PENDING");
        order.setAddressId(addressId);
        
        double total = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        order.setTotalPrice(total);
        
        try {
            var restaurant = restaurantClient.getRestaurantById(order.getRestaurantId());
            order.setRestaurantName(restaurant.getRestaurantName());
        } catch (Exception e) {
            logger.error("Failed to fetch restaurant name for ID: {}", order.getRestaurantId(), e);
            order.setRestaurantName("Unknown Restaurant");
        }
        
        order = orderRepository.save(order);
        
        for (CartItem item : cartItems) {
            item.setOrderId(order.getOrderId());
            cartItemRepository.save(item);
        }
        
        return orderRepository.findById(order.getOrderId()).orElse(order);
    }

    @Transactional
    public Order placeOrder(Long userId) {
        return placeOrder(userId, null);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<Order> getOrdersByRestaurantId(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void rollbackOrder(Long orderId) {
        Order order = getOrderById(orderId);
        // Only rollback if it's still in PENDING status
        if ("PENDING".equals(order.getStatus())) {
            List<CartItem> items = cartItemRepository.findByOrderId(orderId);
            for (CartItem item : items) {
                item.setOrderId(null);
                cartItemRepository.save(item);
            }
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            logger.info("Order {} rolled back and marked as CANCELLED", orderId);
        }
    }
}
