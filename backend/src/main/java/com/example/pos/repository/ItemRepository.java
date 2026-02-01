package com.example.pos.repository;

import com.example.pos.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    List<Item> findByIsActiveTrueOrderByNameAsc();
    List<Item> findAllByOrderByNameAsc();
}
