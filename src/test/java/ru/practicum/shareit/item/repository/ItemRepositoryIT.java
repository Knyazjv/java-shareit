package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User user = new User(1L, "user", "mail@mail.ru");
    User user2 = new User(2L, "name", "ya@mail.ru");
    Item item1 = new Item(1L, user, "1iTem1", "1description1", true, null);
    Item item2 = new Item(2L, user2, "3name2", "2description2ITEm", true, null);
    Item item3 = new Item(3L, user, "3name3", "3description3", true, null);

    @BeforeEach
    void beforeEach() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    void findAllByUserIdTest() {
        List<Item> items = itemRepository.findAllByUserId(1L, PageRequest.of(0, 3));
        equalsItem(item1, items.get(0));
        equalsItem(item3, items.get(1));
        assertEquals(items.size(), 2);
    }

    @Test
    void searchItemsTest() {
        List<Item> items = itemRepository.searchItems("item", PageRequest.of(0, 3));
        equalsItem(item1, items.get(0));
        equalsItem(item2, items.get(1));
        assertEquals(items.size(), 2);
    }

    void equalsItem(Item item, Item otherItem) {
        assertEquals(item.getId(), otherItem.getId());
        assertEquals(item.getName(), otherItem.getName());
        assertEquals(item.getDescription(), otherItem.getDescription());
        assertEquals(item.getUser().getId(), otherItem.getUser().getId());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}