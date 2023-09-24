package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.MappingItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final MappingItemRequest mappingItemRequest;
    private final UserService userService;

    @Transactional
    @Override
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userService.getUserById(userId);
        return mappingItemRequest.toItemRequestDtoResponse(itemRequestRepository
                .save(mappingItemRequest.toItemRequest(null, user, itemRequestDto, LocalDateTime.now())));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWithItemResponse> getItemRequests(Long requestorId) {
        userService.getUserById(requestorId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(requestorId);
        return mappingItemRequest.toItemRequestDtoWithItemResponse(itemRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWithItemResponse> getAllItemRequestsWithPagination(Long requestorId, PageRequest pageRequest) {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findEverythingWithoutRequestor(requestorId, pageRequest);
        return mappingItemRequest.toItemRequestDtoWithItemResponse(itemRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoWithItemResponse getItemRequestById(Long requestId, Long userId) {
        userService.getUserById(userId);
        return mappingItemRequest.toItemRequestDtoWithItemResponse(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest не найден, id:" + requestId)));
    }
}
