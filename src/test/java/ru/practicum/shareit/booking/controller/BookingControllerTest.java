package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingController bookingController;

    Long userId = 1L;
    UserDto userDto1 = new UserDto(1L, "user", "mail@mail.ru");
    ItemDto itemDto1 = new ItemDto(2L, "3name2", "2description2ITEm", true, null);

    LocalDateTime dateTimeStart = LocalDateTime.now().plusDays(1);
    LocalDateTime dateTimeEnd = LocalDateTime.now().plusDays(10);
    BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(1L, dateTimeStart, dateTimeEnd, itemDto1.getId());

    BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(1L, userDto1, itemDto1, BookingStatus.WAITING,
            dateTimeStart, dateTimeEnd);

    @Test
    void createBookingTest() {
        when(bookingService.createBooking(any(), any())).thenReturn(bookingDtoResponse);

        ResponseEntity<BookingDtoResponse> response = bookingController.createBooking(userId, bookingDtoRequest);
        BookingDtoResponse responseBody = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert responseBody != null;
        equalsBookingDtoResponse(bookingDtoResponse, responseBody);
    }

    @Test
    void confirmationBookingTest() {
        when(bookingService.confirmationBooking(any(), any(), any())).thenReturn(bookingDtoResponse);

        ResponseEntity<BookingDtoResponse> response = bookingController
                .confirmationBooking(userId, bookingDtoRequest.getId(), true);
        BookingDtoResponse responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        equalsBookingDtoResponse(bookingDtoResponse, responseBody);
    }

    @Test
    void getBookingByIdTest() {
        when(bookingService.getBookingById(any(), any())).thenReturn(bookingDtoResponse);

        ResponseEntity<BookingDtoResponse> response = bookingController
                .getBookingById(userId, bookingDtoRequest.getId());
        BookingDtoResponse responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        equalsBookingDtoResponse(bookingDtoResponse, responseBody);
    }

    @Test
    void getAllBookingsByStateTest() {
        Exception e = assertThrows(PaginationException.class, () -> bookingController
                .getAllBookingsByState(userId, null, -1, 1));
        assertEquals("RequestParam 'from' is negative", e.getMessage());
        e = assertThrows(PaginationException.class, () -> bookingController
                .getAllBookingsByState(userId, null, 1, -1));
        assertEquals("RequestParam 'size' should be positive", e.getMessage());
    }

    @Test
    void getAllOwnerBookingsTest() {
        Exception e = assertThrows(PaginationException.class, () -> bookingController
                .getAllOwnerBookings(userId, null, -1, 1));
        assertEquals("RequestParam 'from' is negative", e.getMessage());
        e = assertThrows(PaginationException.class, () -> bookingController
                .getAllOwnerBookings(userId, null, 1, -1));
        assertEquals("RequestParam 'size' should be positive", e.getMessage());
    }

    private void equalsBookingDtoResponse(BookingDtoResponse br, BookingDtoResponse otherBr) {
        assertEquals(br.getId(), otherBr.getId());
        assertEquals(br.getStatus(), otherBr.getStatus());
        assertEquals(br.getBooker().getId(), otherBr.getBooker().getId());
        assertEquals(br.getItem().getId(), otherBr.getItem().getId());
        assertEquals(br.getEnd(), otherBr.getEnd());
        assertEquals(br.getStart(), otherBr.getStart());
    }
}