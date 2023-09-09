package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemInfoDto getItemInfoDtoById(Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<ItemInfoDto> getItemsUser(Long userId);

    List<ItemDto> searchItems(String text);

    CommentDtoResponse createComment(Long userId, Long itemId, CommentDtoRequest comment);
}
