package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class MappingItem {
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public Item toItem(Long itemId, Long userId, ItemDto itemDto) {
        if (itemDto.getName().isEmpty()) {
            throw new EmptyException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription().isEmpty()) {
            throw new EmptyException("Описание вещи не может быть пустым");
        }
        return new Item(itemId, userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}
