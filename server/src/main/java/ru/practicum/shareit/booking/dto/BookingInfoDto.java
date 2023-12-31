package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class BookingInfoDto {
        Long id;
        LocalDateTime start;
        LocalDateTime end;
        Long bookerId;
}
