package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    Long id;

    @NotNull
    Long userId;

    @NotNull
    String name;

    @NotNull
    String description;

    @NotNull
    Boolean available;
}
