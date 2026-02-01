package com.example.pos.service;

import com.example.pos.dto.ItemRequest;
import com.example.pos.dto.ItemResponse;
import com.example.pos.entity.Item;
import com.example.pos.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemResponse> listActive() {
        return itemRepository.findByIsActiveTrueOrderByNameAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ItemResponse> listAll() {
        return itemRepository.findAllByOrderByNameAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    public ItemResponse create(ItemRequest request) {
        Item item = new Item();
        item.setName(request.name());
        item.setPrice(request.price());
        item.setCategory(request.category());
        item.setItemCode(request.itemCode());
        item.setPctCode(request.pctCode());
        item.setActive(true);
        return toResponse(itemRepository.save(item));
    }

    public ItemResponse update(UUID id, ItemRequest request) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        item.setName(request.name());
        item.setPrice(request.price());
        item.setCategory(request.category());
        item.setItemCode(request.itemCode());
        item.setPctCode(request.pctCode());
        return toResponse(itemRepository.save(item));
    }

    public void softDelete(UUID id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        item.setActive(false);
        itemRepository.save(item);
    }

    public ItemResponse toggleActive(UUID id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        item.setActive(!item.isActive());
        return toResponse(itemRepository.save(item));
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
            item.getId(),
            item.getName(),
            item.getPrice(),
            item.getCategory(),
            item.getItemCode(),
            item.getPctCode(),
            item.isActive(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
}
