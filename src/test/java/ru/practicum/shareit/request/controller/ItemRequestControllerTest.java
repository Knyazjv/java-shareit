package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    ItemRequestService itemRequestService;

    @InjectMocks
    ItemRequestController itemRequestController;

    private static final Long itemId = 1L;
    private static final ItemDto itemDto = new ItemDto(itemId, "name",
            "description", true, null);
    private static final ItemDto itemDto2 = new ItemDto(itemId, "nameName",
            "desc", false, null);

    Long userId = 1L;
    ItemRequestDto itemRequestDto = new ItemRequestDto("itemRequest");
    ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description",
            1L, LocalDateTime.now());
    List<ItemDto> itemInfoDtos = List.of(itemDto, itemDto2);
    ItemRequestDtoWithItemResponse itemRequestDtoWithItem = new ItemRequestDtoWithItemResponse(1L,
            "description", 1L, LocalDateTime.now(), itemInfoDtos);
    ItemRequestDtoWithItemResponse itemRequestDtoWithItem2 = new ItemRequestDtoWithItemResponse(2L,
            "descriptionDesc", 3L, LocalDateTime.now(), itemInfoDtos);

    @Test
    void createItemRequestTest() {
        when(itemRequestService.createItemRequest(any(), any())).thenReturn(itemRequestDtoResponse);

        ResponseEntity<ItemRequestDtoResponse> response = itemRequestController
                .createItemRequest(itemRequestDto, userId);
        ItemRequestDtoResponse responseBody = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert responseBody != null;
        equalsItemRequestDto(itemRequestDtoResponse, responseBody);
    }

    @Test
    void getAllItemRequestsTest() {
        when(itemRequestService.getItemRequests(any()))
                .thenReturn(List.of(itemRequestDtoWithItem, itemRequestDtoWithItem2));

        ResponseEntity<List<ItemRequestDtoWithItemResponse>> response = itemRequestController
                .getAllItemRequests(userId);
        List<ItemRequestDtoWithItemResponse> responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        equalsItemRequestDtoWithItem(itemRequestDtoWithItem, responseBody.get(0));
        equalsItemRequestDtoWithItem(itemRequestDtoWithItem2, responseBody.get(1));
    }

    @Test
    void getAllItemRequestsWithPaginationTest() {
        when(itemRequestService.getAllItemRequestsWithPagination(any(), any()))
                .thenReturn(List.of(itemRequestDtoWithItem, itemRequestDtoWithItem2));

        ResponseEntity<List<ItemRequestDtoWithItemResponse>> response = itemRequestController
                .getAllItemRequestsWithPagination(0, 10, userId);
        List<ItemRequestDtoWithItemResponse> responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        equalsItemRequestDtoWithItem(itemRequestDtoWithItem, responseBody.get(0));
        equalsItemRequestDtoWithItem(itemRequestDtoWithItem2, responseBody.get(1));
    }

    @Test
    void getAllItemRequestsWithPagination_whenFromAndSizeNegative() {
        Long userId = 1L;
        Exception e = assertThrows(PaginationException.class, () -> itemRequestController
                .getAllItemRequestsWithPagination(-1, 1, userId));
        assertEquals("RequestParam 'from' is negative", e.getMessage());
        e = assertThrows(PaginationException.class, () -> itemRequestController
                .getAllItemRequestsWithPagination(1, -1, userId));
        assertEquals("RequestParam 'size' should be positive", e.getMessage());
    }

    @Test
    void getItemRequestByIdTest() {
        when(itemRequestService.getItemRequestById(any(), any()))
                .thenReturn(itemRequestDtoWithItem);

        ResponseEntity<ItemRequestDtoWithItemResponse> response = itemRequestController
                .getItemRequestById(itemRequestDtoWithItem.getId(), userId);
        ItemRequestDtoWithItemResponse responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        equalsItemRequestDtoWithItem(itemRequestDtoWithItem, responseBody);
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