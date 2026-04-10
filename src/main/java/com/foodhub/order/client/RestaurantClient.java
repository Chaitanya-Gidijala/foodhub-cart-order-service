package com.foodhub.order.client;

import com.foodhub.order.dto.RestaurantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {
    
    @GetMapping("/api/restaurants/id/{id}")
    RestaurantDTO getRestaurantById(@PathVariable Long id);
}
