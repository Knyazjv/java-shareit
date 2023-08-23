package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    Long id;

    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotNull
    Boolean available;
}
