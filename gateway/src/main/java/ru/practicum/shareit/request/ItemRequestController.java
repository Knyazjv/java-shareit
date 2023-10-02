package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                                    @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllItemRequestsWithPagination(
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "RequestParam 'from' is negative") Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "RequestParam 'size' should be positive") Integer size,
            @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getAllItemRequestsWithPagination(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                        @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }

}
