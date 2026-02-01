package com.example.pos.repository;

import com.example.pos.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    Optional<OrderItem> findByOrderIdAndItemId(UUID orderId, UUID itemId);
}
