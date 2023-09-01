package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemDto getItemDtoById(Long itemId);

    List<ItemDto> getItemsUser(Long userId);

    List<ItemDto> searchItem(String text);

    void checkItemIsExist(Long itemId);
}
