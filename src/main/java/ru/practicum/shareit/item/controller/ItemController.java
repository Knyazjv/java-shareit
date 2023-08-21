package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.MappingItem;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final MappingItem mappingItem;

    @Autowired
    public ItemController(ItemService itemService, MappingItem mappingItem) {
        this.itemService = itemService;
        this.mappingItem = mappingItem;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(itemService.createItem(mappingItem.toItem(null, userId, itemDto), userId));
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemDto newItemDto,
                                           @PathVariable Long itemId) {
        Item item = itemService.getItemById(itemId);
        if (!item.getUserId().equals(userId)) {
            throw new NotFoundException("Пользователь не может изменить характеристики вещи.");
        }
        try {
            Item newItem = mappingItem.toItem(itemId, userId,
                    applyUpdateToItemDto(newItemDto, mappingItem.toDto(item)));
            return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(newItem, userId));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemDtoById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemsUser(userId));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam String text) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.searchItem(text));
    }

    private ItemDto applyUpdateToItemDto(ItemDto newItemDto, ItemDto itemDto) throws IllegalAccessException {
        Field[] fields = ItemDto.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(newItemDto) == null) {
                field.set(newItemDto, field.get(itemDto));
            }
        }
        return newItemDto;
    }
}