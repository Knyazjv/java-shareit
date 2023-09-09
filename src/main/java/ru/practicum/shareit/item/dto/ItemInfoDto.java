package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemInfoDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingInfoDto lastBooking;

    private BookingInfoDto nextBooking;

    List<CommentDtoResponse> comments;
}

