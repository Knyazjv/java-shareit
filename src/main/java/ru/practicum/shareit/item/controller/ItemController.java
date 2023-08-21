package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Post /items, userId:{}, item:{}", userId, itemDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemDto newItemDto,
                                           @PathVariable Long itemId) {
        log.info("Patch /items/{}, userId:{}, item:{}", itemId, userId, newItemDto);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(newItemDto, userId, itemId));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        log.info("Get /items/{}", itemId);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemDtoById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get /items, userId:{}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemsUser(userId));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam String text) {
        log.info("Get /items/search, text:{}", text);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.searchItem(text));
    }
}