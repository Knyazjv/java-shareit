package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                                    @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemRequestService.createItemRequest(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoWithItemResponse>> getAllItemRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemRequestService.getItemRequests(userId));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<ItemRequestDtoWithItemResponse>> getAllItemRequestsWithPagination(
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "RequestParam 'from' is negative") Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "RequestParam 'size' should be positive") Integer size,
            @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemRequestService.getAllItemRequestsWithPagination(
                userId,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"))));
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<ItemRequestDtoWithItemResponse> getItemRequestById(@PathVariable Long requestId,
                        @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemRequestService.getItemRequestById(requestId, userId));
    }

}
