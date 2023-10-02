package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Transactional
    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                           @RequestBody BookingDtoRequest booking) {
        log.info("Post /bookings");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(userId, booking));
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<BookingDtoResponse> confirmationBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                    @PathVariable Long bookingId,
                                                    @RequestParam(value = "approved") Boolean approved) {
        log.info("Patch /bookings/{}, userId:{}", bookingId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.confirmationBooking(userId, bookingId, approved));
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                  @PathVariable Long bookingId) {
        log.info("Get /bookings/{}, userId:{}", bookingId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoResponse>> getAllBookingsByState(@RequestHeader(X_SHARER_USER_ID) Long userId,
                       @RequestParam(value = "state") String state,
                       @RequestParam(value = "from", defaultValue = "0") Integer from,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get /bookings, userId:{} , state:{}", userId, state);
        BookingState bookingState = BookingState.from(state);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getAllBookingsByStateWithPagination(userId, bookingState,
                        PageRequest.of(from / size, size)));
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<List<BookingDtoResponse>> getAllOwnerBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
                       @RequestParam(value = "state", defaultValue = "ALL") String state,
                       @RequestParam(value = "from", defaultValue = "0") Integer from,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get /bookings/owner, userId:{} , state:{}", userId, state);
        BookingState bookingState = BookingState.from(state);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getAllOwnerBookingsWithPagination(userId, bookingState,
                        PageRequest.of(from / size, size)));
    }
}
