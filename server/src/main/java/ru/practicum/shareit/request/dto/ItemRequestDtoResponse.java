package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;

}
