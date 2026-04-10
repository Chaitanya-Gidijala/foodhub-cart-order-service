package com.foodhub.order.controller;

import com.foodhub.order.entity.CartItem;
import com.foodhub.order.entity.Order;
import com.foodhub.order.service.OrderCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OrderCartController {
    private final OrderCartService orderCartService;

    @PostMapping("/cart/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem cartItem) {
        return ResponseEntity.ok(orderCartService.addToCart(cartItem));
    }

    @GetMapping("/cart/user/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(orderCartService.getCartByUserId(userId));
    }

    @PatchMapping("/cart/update/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(orderCartService.updateQuantity(cartItemId, quantity));
    }

    @DeleteMapping("/cart/remove/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId) {
        orderCartService.removeCartItem(cartItemId);
        return ResponseEntity.ok("Item removed");
    }

    @DeleteMapping("/cart/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        orderCartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/orders/place/{userId}")
    public ResponseEntity<Order> placeOrder(@PathVariable Long userId, @RequestBody(required = false) Map<String, Object> request) {
        Long addressId = null;
        if (request != null && request.containsKey("addressId")) {
            addressId = Long.valueOf(request.get("addressId").toString());
        }
        return ResponseEntity.ok(orderCartService.placeOrder(userId, addressId));
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderCartService.getOrdersByUserId(userId));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderCartService.getOrderById(orderId));
    }

    @GetMapping("/orders/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getRestaurantOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderCartService.getOrdersByRestaurantId(restaurantId));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return ResponseEntity.ok(orderCartService.updateOrderStatus(orderId, status));
    }

    @PostMapping("/orders/rollback/{orderId}")
    public ResponseEntity<String> rollbackOrder(@PathVariable Long orderId) {
        orderCartService.rollbackOrder(orderId);
        return ResponseEntity.ok("Order rolled back and items restored to cart");
    }
}
