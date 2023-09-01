package ru.practicum.shareit.item.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long itemId = 1L;

    @Override
    public Item createItem(Item item) {
        item.setId(itemId);
        itemMap.put(itemId, item);
        return itemMap.get(itemId++);
    }

    @Override
    public Item updateItem(Item item) {
        itemMap.put(item.getId(), item);
        return itemMap.get(item.getId());
    }

    @Override
    public Item getItemById(Long itemId) {
        if (itemMap.containsKey(itemId)) {
            return itemMap.get(itemId);
        }
        return null;
    }

    @Override
    public List<Item> getItemsUser(Long userId) {
        return itemMap.values()
                .stream()
                .filter(item -> item.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        if (!text.isEmpty()) {
            return itemMap.values()
                    .stream()
                    .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean itemIsExist(Long itemId) {
        return itemMap.containsKey(itemId);
    }
}