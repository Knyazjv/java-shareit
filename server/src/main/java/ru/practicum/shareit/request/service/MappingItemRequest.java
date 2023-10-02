package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.MappingItem;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class MappingItemRequest {
    private final MappingItem mappingItem;

    public ItemRequest toItemRequest(Long id, User requestor, ItemRequestDto itemRequestDto, LocalDateTime dateTime) {
        return new ItemRequest(id,itemRequestDto.getDescription(), requestor, dateTime, null);
    }

    public ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated());
    }

    public ItemRequestDtoWithItemResponse toItemRequestDtoWithItemResponse(ItemRequest itemRequest) {
        List<ItemDto> itemDtoList = itemRequest.getItems().stream()
                .map(mappingItem::toDto)
                .collect(Collectors.toList());

        return new ItemRequestDtoWithItemResponse(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(), itemDtoList);
    }

    public List<ItemRequestDtoWithItemResponse> toItemRequestDtoWithItemResponse(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDtoWithItemResponse)
                .collect(Collectors.toList());
    }
}
