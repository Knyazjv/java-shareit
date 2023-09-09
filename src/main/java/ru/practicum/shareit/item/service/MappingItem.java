package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.booking.service.MappingBooking;
import ru.practicum.shareit.exception.EmptyException;
import ru.practicum.shareit.item.MappingComment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@Component
@RequiredArgsConstructor
public class MappingItem {

    private final MappingBooking mappingBooking;
    private final MappingComment mappingComment;

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public ItemDtoResponse toDtoResponse(Item item, List<Comment> comments) {
        return new ItemDtoResponse(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), comments);
    }

    public Item toItem(Long itemId, User user, ItemDto itemDto) {
        if (itemDto.getName().isEmpty()) {
            throw new EmptyException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription().isEmpty()) {
            throw new EmptyException("Описание вещи не может быть пустым");
        }
        return new Item(itemId, user, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    public ItemInfoDto toItemInfoDto(Item item, List<Booking> bookings, List<Comment> comments) {
        LocalDateTime dateTime = LocalDateTime.now();
        Booking lastBooking = bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isBefore(dateTime) || booking.getEnd().isBefore(dateTime))
                .max(Comparator.comparing(Booking::getStart)).orElse(null);
        Booking nextBooking = bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isAfter(dateTime))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        return new ItemInfoDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                mappingBooking.toBookingInfoDto(lastBooking),
                mappingBooking.toBookingInfoDto(nextBooking),
                mappingComment.toCommentDtoResponses(comments));
    }
}
