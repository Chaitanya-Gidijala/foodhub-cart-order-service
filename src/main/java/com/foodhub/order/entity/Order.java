package com.foodhub.order.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    private Long userId;
    private Long restaurantId;
    private String restaurantName;
    private Double totalPrice;
    private String status;
    private Long addressId;
    
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId")
    private List<CartItem> cartItems;
}
