package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingState;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(Long userId, BookingDtoRequest booking);

    BookingDtoResponse confirmationBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoResponse getBookingById(Long userId, Long bookingId);

    List<BookingDtoResponse> getAllBookingsByState(Long userId, BookingState state);

    List<BookingDtoResponse> getAllOwnerBookings(Long userId, BookingState state);
}
