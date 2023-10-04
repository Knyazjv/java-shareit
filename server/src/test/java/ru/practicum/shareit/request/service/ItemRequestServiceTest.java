package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.service.MappingBooking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.MappingComment;
import ru.practicum.shareit.item.service.MappingItem;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceTest {

    ItemRequestRepository itemRequestRepository;
    UserService userService;
    ItemRequestService itemRequestService;

    MappingBooking mappingBooking = new MappingBooking();
    MappingComment mappingComment = new MappingComment();
    MappingItem mappingItem = new MappingItem(mappingBooking, mappingComment);
    MappingItemRequest mappingItemRequest = new MappingItemRequest(mappingItem);

    User user = new User(3L, "user", "mail@mail.ru");
    LocalDateTime dateTime = LocalDateTime.now();
    ItemRequest itemRequest = new ItemRequest(1L, "description",
            user, dateTime, new ArrayList<>());
    ItemRequest itemRequest2 = new ItemRequest(2L, "2description2",
            user, dateTime.plusDays(2), new ArrayList<>());

    ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description",
            3L, dateTime);
    ItemRequestDto itemRequestDto = new ItemRequestDto("textText");
    private static final Long userId = 1L;

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userService = mock(UserService.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, mappingItemRequest, userService);
    }

    @Test
    void createItemRequestTest() {
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDtoResponse response = itemRequestService.createItemRequest(itemRequestDto, userId);
        equalsItemRequestDto(itemRequestDtoResponse, response);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void createItemRequest_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(itemRequestDto, userId));
        verify(userService, times(1)).getUserById(any());
        verify(itemRequestRepository, times(0)).save(any());
    }

    @Test
    void getItemRequestsTest() {
        when(itemRequestRepository.findAllByRequestor_Id(any())).thenReturn(List.of(itemRequest, itemRequest2));

        List<ItemRequestDtoWithItemResponse> response = itemRequestService.getItemRequests(userId);
        equalsItemRequestDtoWithItem(mappingItemRequest
                .toItemRequestDtoWithItemResponse(itemRequest), response.get(0));
        equalsItemRequestDtoWithItem(mappingItemRequest
                .toItemRequestDtoWithItemResponse(itemRequest2), response.get(1));
        verify(itemRequestRepository, times(1)).findAllByRequestor_Id(any());
    }

    @Test
    void getItemRequests_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequests(userId));
        verify(userService, times(1)).getUserById(any());
        verify(itemRequestRepository, times(0)).findAllByRequestor_Id(any());
    }

    @Test
    void getAllItemRequestsWithPagination() {
        when(itemRequestRepository.findEverythingWithoutRequestor(any(), any()))
                .thenReturn(List.of(itemRequest, itemRequest2));

        List<ItemRequestDtoWithItemResponse> response = itemRequestService
                .getAllItemRequestsWithPagination(userId, null);
        equalsItemRequestDtoWithItem(mappingItemRequest
                .toItemRequestDtoWithItemResponse(itemRequest), response.get(0));
        equalsItemRequestDtoWithItem(mappingItemRequest
                .toItemRequestDtoWithItemResponse(itemRequest2), response.get(1));
        verify(itemRequestRepository, times(1)).findEverythingWithoutRequestor(any(), any());
    }

    @Test
    void getItemRequestByIdTest() {
        when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(itemRequest));

        ItemRequestDtoWithItemResponse response = itemRequestService.getItemRequestById(userId, user.getId());
        equalsItemRequestDtoWithItem(mappingItemRequest
                .toItemRequestDtoWithItemResponse(itemRequest), response);
        verify(itemRequestRepository, times(1)).findById(any());
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_thenNotFoundExceptionThrown() {
        when(itemRequestRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(userId, user.getId()));
        verify(userService, times(1)).getUserById(any());
        verify(itemRequestRepository, times(1)).findById(any());
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(userId, user.getId()));
        verify(userService, times(1)).getUserById(any());
        verify(itemRequestRepository, times(0)).findById(any());
    }

    void equalsItemRequestDto(ItemRequestDtoResponse ir, ItemRequestDtoResponse otherIr) {
        assertEquals(ir.getId(), otherIr.getId());
        assertEquals(ir.getDescription(), otherIr.getDescription());
        assertEquals(ir.getRequestorId(), otherIr.getRequestorId());
        assertEquals(ir.getCreated(), otherIr.getCreated());
    }

    void equalsItemRequestDtoWithItem(ItemRequestDtoWithItemResponse ir, ItemRequestDtoWithItemResponse otherIr) {
        assertEquals(ir.getId(), otherIr.getId());
        assertEquals(ir.getDescription(), otherIr.getDescription());
        assertEquals(ir.getRequestorId(), otherIr.getRequestorId());
        assertEquals(ir.getCreated(), otherIr.getCreated());
    }
}