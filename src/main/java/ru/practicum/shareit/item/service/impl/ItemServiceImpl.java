package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.MappingItem;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final MappingItem mappingItem;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, MappingItem mappingItem, UserService userService) {
        this.itemRepository = itemRepository;
        this.mappingItem = mappingItem;
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(Item item, Long userId) {
        userService.checkUserIsExist(userId);
        return mappingItem.toDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(Item item, Long userId) {
        userService.checkUserIsExist(userId);
        checkItemIsExist(item.getId());
        return mappingItem.toDto(itemRepository.updateItem(item));
    }

    @Override
    public Item getItemById(Long itemId) {
        checkItemIsExist(itemId);
        return itemRepository.getItemById(itemId);
    }

    @Override
    public ItemDto getItemDtoById(Long itemId) {
        checkItemIsExist(itemId);
        return mappingItem.toDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsUser(Long userId) {
        userService.checkUserIsExist(userId);
        return itemRepository.getItemsUser(userId).stream().
                map(mappingItem::toDto).
                collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text).stream().
                map(mappingItem::toDto).
                collect(Collectors.toList());
    }

    @Override
    public void checkItemIsExist(Long itemId) {
        if (!itemRepository.itemIsExist(itemId)) {
            throw new NotFoundException("Вещь не найдена, id: " + itemId);
        }
    }
}