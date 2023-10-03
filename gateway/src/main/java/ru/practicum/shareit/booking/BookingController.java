package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BookingStateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.enumBooking.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

	@Transactional
	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
									@Valid @RequestBody BookingDtoRequest booking) {
		log.info("Post /bookings");
		return bookingClient.createBooking(userId, booking);
	}

	@PatchMapping(value = "/{bookingId}")
	public ResponseEntity<Object> confirmationBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
									@PathVariable Long bookingId,
									@RequestParam(value = "approved") Boolean approved) {
		log.info("Patch /bookings/{}, userId:{}", bookingId, userId);
		return bookingClient.confirmationBooking(userId, bookingId, approved);
	}

	@GetMapping(value = "/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId,
									@PathVariable Long bookingId) {
		log.info("Get /bookings/{}, userId:{}", bookingId, userId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsByState(@RequestHeader(X_SHARER_USER_ID) Long userId,
					@RequestParam(value = "state", defaultValue = "ALL") String state,
					@RequestParam(value = "from", defaultValue = "0")
					@Min(value = 0, message = "RequestParam 'from' is negative") Integer from,
					@RequestParam(value = "size", defaultValue = "10")
					@Min(value = 1, message = "RequestParam 'size' should be positive") Integer size) {
		log.info("Get /bookings, userId:{} , state:{}", userId, state);
		BookingState bookingState = BookingState.from(state);
		if (bookingState == null) {
			throw new BookingStateException("Unknown state: " + state);
		}
		return bookingClient.getAllBookingsByStateWithPagination(userId, bookingState,from, size);
	}

	@GetMapping(value = "/owner")
	public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
					@RequestParam(value = "state", defaultValue = "ALL") String state,
					@RequestParam(value = "from", defaultValue = "0")
					@Min(value = 0, message = "RequestParam 'from' is negative") Integer from,
					@RequestParam(value = "size", defaultValue = "10")
					@Min(value = 1, message = "RequestParam 'size' should be positive") Integer size) {
		log.info("Get /bookings/owner, userId:{} , state:{}", userId, state);
		BookingState bookingState = BookingState.from(state);
		if (bookingState == null) {
			throw new BookingStateException("Unknown state: " + state);
		}
		return bookingClient.getAllOwnerBookingsWithPagination(userId, bookingState, from, size);
	}
}
