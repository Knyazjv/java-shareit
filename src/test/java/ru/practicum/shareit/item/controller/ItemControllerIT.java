package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.controller.ItemControllerTest.getBookingInfoDto;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerIT {

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private static final Long userId = 1L;
    private static final Long itemId = 1L;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void createItemTest() {
        ItemDto itemDto = new ItemDto(itemId, "name", "description", true, null);
        when(itemService.createItem(any(), any())).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1)).createItem(any(), any());
    }

    @SneakyThrows
    @Test
    void createItem_whenItemIsNotValid_thenResponseStatusIsBadRequest() {
        ItemDto itemDto = new ItemDto(itemId, "", "description", true, null);

        performCreatedInvalidItem(itemDto);

        itemDto.setName("name");
        itemDto.setDescription("");
        performCreatedInvalidItem(itemDto);

        itemDto.setAvailable(null);
        performCreatedInvalidItem(itemDto);

        verify(itemService, times(0)).createItem(any(), any());
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        ItemDto itemDto = new ItemDto(itemId, "name", "description", true, null);
        when(itemService.updateItem(any(), any(), any())).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1)).updateItem(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getItemByIdTest() {
        ItemInfoDto itemInfoDto = new ItemInfoDto(1L, "name", "description", true,
                getBookingInfoDto(), getBookingInfoDto(), new ArrayList<>());
        when(itemService.getItemInfoDtoById(any(), any())).thenReturn(itemInfoDto);

        String result = mockMvc.perform(get("/items/1")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemInfoDto), result);
        verify(itemService, times(1)).getItemInfoDtoById(any(), any());
    }

    @SneakyThrows
    @Test
    void getItemsUserTest() {
        ItemInfoDto itemInfoDto = new ItemInfoDto(1L, "name", "description", true,
                getBookingInfoDto(), getBookingInfoDto(), new ArrayList<>());
        ItemInfoDto itemInfoDto2 = new ItemInfoDto(2L, "nameName", "desc", false,
                getBookingInfoDto(), getBookingInfoDto(), new ArrayList<>());
        List<ItemInfoDto> itemInfoDtos = List.of(itemInfoDto, itemInfoDto2);
        when(itemService.getItemsUserWithPagination(any(), any())).thenReturn(itemInfoDtos);

        String result = mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemInfoDtos), result);
        verify(itemService, times(1)).getItemsUserWithPagination(any(), any());

    }

    @SneakyThrows
    @Test
    void searchItemTest() {
        ItemDto itemDto = new ItemDto(itemId, "name", "description", true, null);
        ItemDto itemDto2 = new ItemDto(itemId, "nameName", "desc", false, null);
        List<ItemDto> itemInfoDtos = List.of(itemDto, itemDto2);
        when(itemService.searchItemsWithPagination(any(), any())).thenReturn(itemInfoDtos);

        String result = mockMvc.perform(get("/items/search")
                        .param("text", "searchSearch")
                        .param("from", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemInfoDtos), result);
        verify(itemService, times(1)).searchItemsWithPagination(any(), any());

    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        LocalDateTime dateTime = LocalDateTime.now();
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse(1L, "Text",
                "name", dateTime);
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("text");
        when(itemService.createComment(any(), any(), any())).thenReturn(commentDtoResponse);

        String result = mockMvc.perform(post("/items/1/comment")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(commentDtoResponse), result);
        verify(itemService, times(1)).createComment(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentRequestNotValid_thenResponseStatusIsBadRequest() {
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("");

        mockMvc.perform(post("/items/1/comment")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, times(0)).createComment(any(), any(), any());
    }

    private void performCreatedInvalidItem(ItemDto itemDto) throws Exception {
        when(itemService.createItem(any(), any())).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }
}