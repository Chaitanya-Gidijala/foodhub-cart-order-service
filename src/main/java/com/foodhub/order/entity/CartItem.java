package com.foodhub.order.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cart_items")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    
    private Long userId;
    private Long menuItemId;
    private String name;
    private Double price;
    private Integer quantity;
    private Long restaurantId;
    
    @Column(nullable = true)
    private Long orderId;
}
