package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoWithItemResponse> getItemRequests(Long requestorId);

    List<ItemRequestDtoWithItemResponse> getAllItemRequestsWithPagination(Long requestorId, PageRequest pageRequest);

    ItemRequestDtoWithItemResponse getItemRequestById(Long requestId, Long userId);
}
