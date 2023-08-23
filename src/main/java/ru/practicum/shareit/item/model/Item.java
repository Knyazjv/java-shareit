package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    Long id;
    Long userId;
    String name;
    String description;
    Boolean available;
}
