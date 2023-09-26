package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemInfoDto getItemInfoDtoById(Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<ItemInfoDto> getItemsUserWithPagination(Long userId, PageRequest pageRequest);

    List<ItemDto> searchItemsWithPagination(String text, PageRequest pageRequest);

    CommentDtoResponse createComment(Long userId, Long itemId, CommentDtoRequest comment);
}
