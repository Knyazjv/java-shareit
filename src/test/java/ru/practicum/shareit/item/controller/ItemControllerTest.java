package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    ItemDto itemDto = new ItemDto(1L, "name", "description", true, null);
    ItemDto emptyItemDto = new ItemDto(null, null, null, null, null);
    ItemInfoDto itemInfoDto = new ItemInfoDto(1L, "name", "description", true,
            getBookingInfoDto(), getBookingInfoDto(), new ArrayList<>());
    CommentDtoResponse commentDtoResponse = new CommentDtoResponse(1L, "comment", "name", LocalDateTime.now());

    @Test
    void createItem_whenInvoked_thenResponseStatusCreatedWithItemDtoInBody() {
        when(itemService.createItem(any(), any())).thenReturn(itemDto);

        ResponseEntity<ItemDto> response = itemController.createItem(1L, emptyItemDto);
        ItemDto itemDtoResponse = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert itemDtoResponse != null;
        equalsItemDto(itemDto, itemDtoResponse);
    }

    @Test
    void updateItem_whenInvoked_thenResponseStatusOkWithItemDtoInBody() {
        when(itemService.updateItem(any(), any(), any())).thenReturn(itemDto);

        ResponseEntity<ItemDto> response = itemController.updateItem(1L, emptyItemDto, 1L);
        ItemDto itemDtoResponse = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert itemDtoResponse != null;
        equalsItemDto(itemDto, itemDtoResponse);
    }

    @Test
    void getItemById_whenInvoked_thenResponseStatusOkWithItemInfoDtoInBody() {
        when(itemService.getItemInfoDtoById(any(), any())).thenReturn(itemInfoDto);

        ResponseEntity<ItemInfoDto> response = itemController.getItemById(1L, 1L);
        ItemInfoDto itemDtoResponse = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert itemDtoResponse != null;
        equalsItemInfoDto(itemInfoDto, itemDtoResponse);
    }

    @Test
    void getItemsUser_whenInvoked_thenResponseStatusOkWithListItemInfoDtoInBody() {
        List<ItemInfoDto> itemDtos = List.of(itemInfoDto);
        when(itemService.getItemsUserWithPagination(any(), any())).thenReturn(itemDtos);

        ResponseEntity<List<ItemInfoDto>> response = itemController.getItemsUser(1L, 0, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDtos.size(), Objects.requireNonNull(response.getBody()).size());
        equalsItemInfoDto(itemInfoDto, response.getBody().get(0));
    }

    @Test
    void searchItem_whenInvoked_thenResponseStatusOkWithItemDtoInBody() {
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemService.searchItemsWithPagination(any(), any())).thenReturn(itemDtos);

        ResponseEntity<List<ItemDto>> response = itemController.searchItem("text", 1, 1);
        List<ItemDto> itemDtosResponse = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert itemDtosResponse != null;
        equalsItemDto(itemDto, itemDtosResponse.get(0));
        assertEquals(itemDtos.size(), itemDtosResponse.size());
    }

    @Test
    void searchItem_whenFromAndSizeNegative() {
        assertThrows(IllegalArgumentException.class, () -> itemController.searchItem("text", -1, 1));
        assertThrows(IllegalArgumentException.class, () -> itemController.searchItem("text", 0, -1));
    }

    @Test
    void createComment_whenInvoked_thenResponseStatusOkCommentDtoInBody() {
        when(itemService.createComment(any(), any(), any())).thenReturn(commentDtoResponse);

        ResponseEntity<CommentDtoResponse> response = itemController.createComment(1L, 1L, new CommentDtoRequest("j"));
        CommentDtoResponse commentDtoResponse2 = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert commentDtoResponse2 != null;
        assertEquals(commentDtoResponse.getId(), commentDtoResponse2.getId());
        assertEquals(commentDtoResponse.getText(), commentDtoResponse2.getText());
        assertEquals(commentDtoResponse.getAuthorName(), commentDtoResponse2.getAuthorName());
        assertEquals(commentDtoResponse.getCreated(), commentDtoResponse2.getCreated());
    }

    public void equalsItemDto(ItemDto itemDto, ItemDto itemDtoResponse) {
        assertEquals(itemDto.getId(), itemDtoResponse.getId());
        assertEquals(itemDto.getName(), itemDtoResponse.getName());
        assertEquals(itemDto.getDescription(), itemDtoResponse.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoResponse.getAvailable());
        assertEquals(itemDto.getRequestId(), itemDtoResponse.getRequestId());
    }

    public static BookingInfoDto getBookingInfoDto() {
        LocalDateTime dateTime = LocalDateTime.now();
        return new BookingInfoDto(1L, dateTime, dateTime.plusDays(2), 1L);
    }

    private void equalsItemInfoDto(ItemInfoDto itemDto, ItemInfoDto itemDtoResponse) {
        assertEquals(itemDto.getId(), itemDtoResponse.getId());
        assertEquals(itemDto.getName(), itemDtoResponse.getName());
        assertEquals(itemDto.getDescription(), itemDtoResponse.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoResponse.getAvailable());
        assertEquals(itemDto.getLastBooking().getId(), itemDtoResponse.getLastBooking().getId());
        assertEquals(itemDto.getNextBooking().getId(), itemDtoResponse.getNextBooking().getId());
    }
}