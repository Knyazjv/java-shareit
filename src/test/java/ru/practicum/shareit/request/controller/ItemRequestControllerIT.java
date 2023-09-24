package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerIT {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;


    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long userId = 1L;
    private static final Long itemId = 1L;
    private static final ItemDto itemDto = new ItemDto(itemId, "name",
            "description", true, null);
    private static final ItemDto itemDto2 = new ItemDto(itemId, "nameName",
            "desc", false, null);

    @SneakyThrows
    @Test
    void createItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("itemRequest");
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description",
                1L, LocalDateTime.now());
        when(itemRequestService.createItemRequest(any(),any())).thenReturn(itemRequestDtoResponse);

        String result = mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDtoResponse), result);
        verify(itemRequestService, times(1)).createItemRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsTest() {
        List<ItemDto> itemInfoDtos = List.of(itemDto, itemDto2);
        ItemRequestDtoWithItemResponse itemRequestDtoResponse = new ItemRequestDtoWithItemResponse(1L,
                "description", 1L, LocalDateTime.now(), itemInfoDtos);
        ItemRequestDtoWithItemResponse itemRequestDtoResponse2 = new ItemRequestDtoWithItemResponse(2L,
                "descriptionDesc", 3L, LocalDateTime.now(), itemInfoDtos);
        List<ItemRequestDtoWithItemResponse> itemRequestDtoResponses = List.of(itemRequestDtoResponse,
                itemRequestDtoResponse2);
        when(itemRequestService.getItemRequests(any())).thenReturn(itemRequestDtoResponses);

        String result = mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDtoResponses), result);
        verify(itemRequestService, times(1)).getItemRequests(any());
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsWithPaginationTest() {
        List<ItemDto> itemInfoDtos = List.of(itemDto, itemDto2);
        ItemRequestDtoWithItemResponse itemRequestDtoResponse = new ItemRequestDtoWithItemResponse(1L,
                "description", 1L, LocalDateTime.now(), itemInfoDtos);
        ItemRequestDtoWithItemResponse itemRequestDtoResponse2 = new ItemRequestDtoWithItemResponse(2L,
                "descriptionDesc", 3L, LocalDateTime.now(), itemInfoDtos);
        List<ItemRequestDtoWithItemResponse> itemRequestDtoResponses = List.of(itemRequestDtoResponse,
                itemRequestDtoResponse2);
        when(itemRequestService.getAllItemRequestsWithPagination(any(),any())).thenReturn(itemRequestDtoResponses);

        String result = mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDtoResponses), result);
        verify(itemRequestService, times(1)).getAllItemRequestsWithPagination(any(), any());
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        List<ItemDto> itemInfoDtos = List.of(itemDto, itemDto2);
        ItemRequestDtoWithItemResponse itemRequestDtoResponse = new ItemRequestDtoWithItemResponse(1L,
                "description", 1L, LocalDateTime.now(), itemInfoDtos);
        when(itemRequestService.getItemRequestById(any(),any())).thenReturn(itemRequestDtoResponse);

        String result = mockMvc.perform(get("/requests/1")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDtoResponse), result);
        verify(itemRequestService, times(1)).getItemRequestById(any(), any());
    }
}