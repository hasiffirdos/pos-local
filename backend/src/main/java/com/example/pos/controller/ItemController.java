package com.example.pos.controller;

import com.example.pos.dto.ItemRequest;
import com.example.pos.dto.ItemResponse;
import com.example.pos.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> listItems(@RequestParam(value = "includeInactive", defaultValue = "false") boolean includeInactive) {
        return includeInactive ? itemService.listAll() : itemService.listActive();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) {
        return itemService.create(request);
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@PathVariable UUID id, @Valid @RequestBody ItemRequest request) {
        return itemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable UUID id) {
        itemService.softDelete(id);
    }

    @PatchMapping("/{id}/toggle-active")
    public ItemResponse toggleActive(@PathVariable UUID id) {
        return itemService.toggleActive(id);
    }
}
