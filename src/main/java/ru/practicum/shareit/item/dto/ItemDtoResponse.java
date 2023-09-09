package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDtoResponse {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    List<Comment> comments;
}
