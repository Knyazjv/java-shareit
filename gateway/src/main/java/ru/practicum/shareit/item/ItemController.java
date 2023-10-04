package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Post /items, userId:{}, item:{}", userId, itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                           @RequestBody ItemDto newItemDto,
                                           @PathVariable Long itemId) {
        log.info("Patch /items/{}, userId:{}, item:{}", itemId, userId, newItemDto);
        return itemClient.updateItem(newItemDto, userId, itemId);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                            @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Get /items/{}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsUser(@RequestHeader(X_SHARER_USER_ID) Long userId,
                   @RequestParam(value = "from", defaultValue = "0")
                   @Min(value = 0, message = "RequestParam 'from' is negative") Integer from,
                   @RequestParam(value = "size", defaultValue = "10")
                   @Min(value = 1, message = "RequestParam 'size' should be positive") Integer size) {
        log.info("Get /items, userId:{}", userId);
        return itemClient.getItemsUserWithPagination(userId, from, size);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                   @RequestParam(value = "from", defaultValue = "0")
                   @Min(value = 0, message = "RequestParam 'from' is negative") Integer from,
                   @RequestParam(value = "size", defaultValue = "10")
                   @Min(value = 1, message = "RequestParam 'size' should be positive") Integer size) {
        log.info("Get /items/search, text:{}", text);
        return itemClient.searchItemsWithPagination(text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                               @PathVariable Long itemId,
                                               @Valid @RequestBody CommentDtoRequest comment) {
        log.info("Post /items/{}/comment, userId:{}", itemId, userId);
        return itemClient.createComment(userId, itemId, comment);
    }
}