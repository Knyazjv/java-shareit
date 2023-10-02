package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerIT {

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    private static final Long userId = 1L;
    private static final Long itemId = 1L;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");
    private static final ItemDto itemDto = new ItemDto(itemId, "name", "description", true, null);

    @SneakyThrows
    @Test
    void createBookingTest() {
        BookingDtoResponse bookingResponse = new BookingDtoResponse(1L, userDto, itemDto, BookingStatus.WAITING,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(12), 1L);
        when(bookingService.createBooking(any(),any())).thenReturn(bookingResponse);

        String result = mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResponse), result);
        verify(bookingService, times(1)).createBooking(any(), any());
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingDtoRequestIsNotValid_thenResponseStatusIsBadRequest() {
        BookingDtoResponse bookingResponse = new BookingDtoResponse(1L, userDto, itemDto, BookingStatus.WAITING,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(12), null);
        when(bookingService.createBooking(any(),any())).thenReturn(bookingResponse);

        performCreateBookingInvalid(bookingDtoRequest);

        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().minusDays(10));
        performCreateBookingInvalid(bookingDtoRequest);

        bookingDtoRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().minusDays(10));
        performCreateBookingInvalid(bookingDtoRequest);

        verify(bookingService, times(0)).createBooking(any(), any());
    }

    private void performCreateBookingInvalid(BookingDtoRequest bookingDtoRequest) throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void confirmationBookingTest() {
        BookingDtoResponse bookingResponse = new BookingDtoResponse(1L, userDto, itemDto, BookingStatus.WAITING,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(bookingService.confirmationBooking(any(),any(), any())).thenReturn(bookingResponse);

        String result = mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, userId)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResponse), result);
        verify(bookingService, times(1)).confirmationBooking(any(),any(), any());
    }

    @SneakyThrows
    @Test
    void confirmationBooking_whenParamApprovedMissing_thenResponseStatusIsBadRequest() {
        BookingDtoResponse bookingResponse = new BookingDtoResponse(1L, userDto, itemDto, BookingStatus.WAITING,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(bookingService.confirmationBooking(any(),any(), any())).thenReturn(bookingResponse);

        mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(0)).confirmationBooking(any(),any(), any());
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        BookingDtoResponse bookingResponse = new BookingDtoResponse(1L, userDto, itemDto, BookingStatus.WAITING,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(bookingService.getBookingById(any(), any())).thenReturn(bookingResponse);

        String result = mockMvc.perform(get("/bookings/1")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResponse), result);
        verify(bookingService, times(1)).getBookingById(any(), any());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByStateTest() {
        BookingDtoResponse bookingResponse = new BookingDtoResponse(1L, userDto, itemDto, BookingStatus.WAITING,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        BookingDtoResponse bookingResponse2 = new BookingDtoResponse(2L, userDto, itemDto, BookingStatus.APPROVED,
                LocalDateTime.now().plusDays(12), LocalDateTime.now().plusDays(35));
        List<BookingDtoResponse> bookingDtoResponseList = List.of(bookingResponse, bookingResponse2);
        when(bookingService.getAllBookingsByStateWithPagination(any(),any(),any())).thenReturn(bookingDtoResponseList);

        String result = mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDtoResponseList), result);
        verify(bookingService, times(1))
                .getAllBookingsByStateWithPagination(any(),any(), any());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByStateTest_whenParamFromIsNegative_thenResponseIsBadRequest() {
       mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", "-1")
                        .param("size", "15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
       verify(bookingService, never()).getAllBookingsByStateWithPagination(any(),any(), any());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByStateTest_whenParamSizeIsNegative_thenResponseIsBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", "0")
                        .param("size", "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getAllBookingsByStateWithPagination(any(),any(), any());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookingsTest() {
        BookingDtoResponse bookingResponse = new BookingDtoResponse(1L, userDto, itemDto, BookingStatus.WAITING,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        BookingDtoResponse bookingResponse2 = new BookingDtoResponse(2L, userDto, itemDto, BookingStatus.APPROVED,
                LocalDateTime.now().plusDays(12), LocalDateTime.now().plusDays(35));
        List<BookingDtoResponse> bookingDtoResponseList = List.of(bookingResponse, bookingResponse2);
        when(bookingService.getAllOwnerBookingsWithPagination(any(),any(),any())).thenReturn(bookingDtoResponseList);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDtoResponseList), result);
        verify(bookingService, times(1))
                .getAllOwnerBookingsWithPagination(any(),any(), any());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookingsTest_whenParamSizeIsNegative_thenResponseIsBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", "0")
                        .param("size", "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getAllOwnerBookingsWithPagination(any(),any(), any());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookingsTest_whenParamFromIsNegative_thenResponseIsBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", "-1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getAllOwnerBookingsWithPagination(any(),any(), any());
    }
}