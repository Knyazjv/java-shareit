package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    BookingClient bookingClient;

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
        Mockito.when(bookingClient.createBooking(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ResponseEntity
                .status(HttpStatus.CREATED).body(bookingDtoResponse));

        ResponseEntity<Object> response = bookingController.createBooking(userId, bookingDtoRequest);
        BookingDtoResponse responseBody = (BookingDtoResponse) response.getBody();
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert responseBody != null;
        equalsBookingDtoResponse(bookingDtoResponse, responseBody);
    }

    @Test
    void confirmationBookingTest() {
        Mockito.when(bookingClient.confirmationBooking(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ResponseEntity
                .status(HttpStatus.OK).body(bookingDtoResponse));

        ResponseEntity<Object> response = bookingController
                .confirmationBooking(userId, bookingDtoRequest.getId(), true);
        BookingDtoResponse responseBody = (BookingDtoResponse) response.getBody();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        equalsBookingDtoResponse(bookingDtoResponse, responseBody);
    }

    @Test
    void getBookingByIdTest() {
        Mockito.when(bookingClient.getBookingById(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ResponseEntity
                .status(HttpStatus.OK).body(bookingDtoResponse));

        ResponseEntity<Object> response = bookingController
                .getBookingById(userId, bookingDtoRequest.getId());
        BookingDtoResponse responseBody = (BookingDtoResponse) response.getBody();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        equalsBookingDtoResponse(bookingDtoResponse, responseBody);
    }

    @Test
    void getAllBookingsByStateTest_whenStateUnknown() {
        Assertions.assertThrows(BookingStateException.class, () -> bookingController
                .getAllBookingsByState(userId,"123", 0, 10));
        Mockito.verify(bookingClient, Mockito.never()).getAllBookingsByStateWithPagination(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void getAllOwnerBookingsTest_whenStateUnknown() {
        Assertions.assertThrows(BookingStateException.class, () -> bookingController
                .getAllOwnerBookings(userId,"123", 0, 10));
        Mockito.verify(bookingClient, Mockito.never()).getAllOwnerBookingsWithPagination(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
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