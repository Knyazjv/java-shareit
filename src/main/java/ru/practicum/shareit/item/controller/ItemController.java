package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Post /items, userId:{}, item:{}", userId, itemDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                           @RequestBody ItemDto newItemDto,
                                           @PathVariable Long itemId) {
        log.info("Patch /items/{}, userId:{}, item:{}", itemId, userId, newItemDto);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(newItemDto, userId, itemId));
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<ItemInfoDto> getItemById(@PathVariable Long itemId,
                                                   @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Get /items/{}", itemId);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemInfoDtoById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemInfoDto>> getItemsUser(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Get /items, userId:{}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemsUser(userId));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam String text) {
        log.info("Get /items/search, text:{}", text);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.searchItems(text));
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> createComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                            @PathVariable Long itemId,
                                                            @Valid @RequestBody CommentDtoRequest comment) {
        log.info("Post /items/{}/comment, userId:{}", itemId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(itemService.createComment(userId, itemId, comment));
    }
}