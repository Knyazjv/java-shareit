package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    ItemRequestClient itemRequestClient;

    @InjectMocks
    ItemRequestController itemRequestController;

    private static final Long itemId = 1L;
    private static final ItemDto itemDto = new ItemDto(itemId, "name",
            "description", true, null);
    private static final  List<ItemDto> itemDtos = List.of(itemDto);
    ObjectMapper objectMapper = new ObjectMapper();

    Long userId = 1L;
    ItemRequestDto itemRequestDto = new ItemRequestDto("itemRequest");

    @SneakyThrows
    @Test
    void createItemRequestTest() {
        when(itemRequestClient.createItemRequest(any(), any())).thenReturn(ResponseEntity
                .status(HttpStatus.CREATED).body(itemRequestDto));

        ResponseEntity<Object> response = itemRequestController
                .createItemRequest(itemRequestDto, userId);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString(itemRequestDto));
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsTest() {
        when(itemRequestClient.getItemRequests(any())).thenReturn(ResponseEntity
                        .status(HttpStatus.OK).body(itemDtos));

        ResponseEntity<Object> response = itemRequestController.getAllItemRequests(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString(itemDtos));
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsWithPaginationTest() {
        when(itemRequestClient.getAllItemRequestsWithPagination(any(), any(), any())).thenReturn(ResponseEntity
                        .status(HttpStatus.OK).body(itemDtos));

        ResponseEntity<Object> response = itemRequestController
                .getAllItemRequestsWithPagination(0, 10, userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString(itemDtos));
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        when(itemRequestClient.getItemRequestById(any(), any())).thenReturn(ResponseEntity
                        .status(HttpStatus.OK).body(itemDto));

        ResponseEntity<Object> response = itemRequestController.getItemRequestById(itemDto.getId(), userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString(itemDto));
    }
}