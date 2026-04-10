package com.foodhub.order.repository;

import com.foodhub.order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdAndOrderIdIsNull(Long userId);
    void deleteByUserIdAndOrderIdIsNull(Long userId);
}
