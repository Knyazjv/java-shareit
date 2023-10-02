package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    ItemClient itemClient;

    @InjectMocks
    ItemController itemController;

    ItemDto itemDto = new ItemDto(1L, "name", "description", true, null);
    ItemDto emptyItemDto = new ItemDto(null, null, null, null, null);
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createItem_whenInvoked_thenResponseStatusCreatedWithItemDtoInBody() {
        when(itemClient.createItem(any(), any())).thenReturn(ResponseEntity
                .status(HttpStatus.CREATED).body(itemDto));

        ResponseEntity<Object> response = itemController.createItem(1L, emptyItemDto);
        ItemDto itemDtoResponse = (ItemDto) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert itemDtoResponse != null;
        equalsItemDto(itemDto, itemDtoResponse);
    }

    @Test
    void updateItem_whenInvoked_thenResponseStatusOkWithItemDtoInBody() {
        when(itemClient.updateItem(any(), any(), any())).thenReturn(ResponseEntity
                .status(HttpStatus.OK).body(itemDto));

        ResponseEntity<Object> response = itemController.updateItem(1L, emptyItemDto, 1L);
        ItemDto itemDtoResponse = (ItemDto) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert itemDtoResponse != null;
        equalsItemDto(itemDto, itemDtoResponse);
    }

    @SneakyThrows
    @Test
    void getItemById_whenInvoked_thenResponseStatusOkWithItemInfoDtoInBody() {
        when(itemClient.getItemById(any(), any())).thenReturn(ResponseEntity
                .status(HttpStatus.OK).body(itemDto));

        ResponseEntity<Object> response = itemController.getItemById(1L, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(response.getBody()), objectMapper.writeValueAsString(itemDto));
    }

    @SneakyThrows
    @Test
    void getItemsUser_whenInvoked_thenResponseStatusOkWithListItemInfoDtoInBody() {
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemClient.getItemsUserWithPagination(any(), any(), any())).thenReturn(ResponseEntity
                .status(HttpStatus.OK).body(itemDtos));

        ResponseEntity<Object> response = itemController.getItemsUser(1L, 0, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(response.getBody()), objectMapper.writeValueAsString(itemDtos));
    }

    @SneakyThrows
    @Test
    void searchItem_whenInvoked_thenResponseStatusOkWithItemDtoInBody() {
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemClient.searchItemsWithPagination(any(), any(), any())).thenReturn(ResponseEntity
                .status(HttpStatus.OK).body(itemDtos));

        ResponseEntity<Object> response = itemController.searchItem("text", 1, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(response.getBody()), objectMapper.writeValueAsString(itemDtos));
    }

    public void equalsItemDto(ItemDto itemDto, ItemDto itemDtoResponse) {
        assertEquals(itemDto.getId(), itemDtoResponse.getId());
        assertEquals(itemDto.getName(), itemDtoResponse.getName());
        assertEquals(itemDto.getDescription(), itemDtoResponse.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoResponse.getAvailable());
        assertEquals(itemDto.getRequestId(), itemDtoResponse.getRequestId());
    }
}