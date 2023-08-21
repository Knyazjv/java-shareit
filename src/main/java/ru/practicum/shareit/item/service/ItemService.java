package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Item item, Long userId);

    ItemDto updateItem(Item item, Long userId);

    Item getItemById(Long itemId);

    ItemDto getItemDtoById(Long itemId);

    List<ItemDto> getItemsUser(Long userId);

    List<ItemDto> searchItem(String text);

    void checkItemIsExist(Long itemId);
}
