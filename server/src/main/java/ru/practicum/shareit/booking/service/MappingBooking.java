package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class MappingBooking {

    public BookingInfoDto toBookingInfoDto(Booking booking) {
        if (booking == null) return null;
        return new BookingInfoDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId());
    }

    public Booking toBooking(Long bookingId, User user, Item item, BookingStatus bookingStatus, BookingDtoRequest bdr) {
        return new Booking(bookingId, user, item, bookingStatus, bdr.getStart(), bdr.getEnd());
    }

    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        User user = booking.getBooker();
        Item item = booking.getItem();
        return new BookingDtoResponse(booking.getId(),
                new UserDto(user.getId(), user.getName(), user.getEmail()),
                new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd());
    }
}
