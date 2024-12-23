package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ItemDTO;
import com.example.demo.entity.Item;
import com.example.demo.repository.ItemRepository;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    // Get all items
    @GetMapping
    public List<ItemDTO> getAllItems(@RequestParam(required = false) String keyword) {
        List<Item> items;
        if (keyword != null) {
            items = itemRepository.findByNameContainingIgnoreCase(keyword);
        } else {
            items = itemRepository.findAll();
        }

        return items.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get item by ID
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));
        return ResponseEntity.ok(convertToDTO(item));
    }

    // Create new item
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        Item item = convertToEntity(itemDTO);
        Item savedItem = itemRepository.save(item);
        return ResponseEntity.ok(convertToDTO(savedItem));
    }

    // Update existing item
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));

        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());

        Item updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(convertToDTO(updatedItem));
    }

    // Delete item by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemRepository.deleteById(id);
        return ResponseEntity.ok("Item with ID " + id + " has been deleted successfully.");
    }

    // Helper method: Convert Entity to DTO
    private ItemDTO convertToDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setName(item.getName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setPrice(item.getPrice());
        return itemDTO;
    }

    // Helper method: Convert DTO to Entity
    private Item convertToEntity(ItemDTO itemDTO) {
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        return item;
    }
}
