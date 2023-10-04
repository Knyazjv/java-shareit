package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryIT {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    User user2;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "requestor", "mail@mail.ru");
        userRepository.save(user);
        user2 = new User(2L, "name", "mail@yandex.ru");
        userRepository.save(user2);

        itemRequest = new ItemRequest(1L, "text", user2,
                LocalDateTime.now(), null);
        itemRequestRepository.save(itemRequest);
        itemRequest2 = new ItemRequest(2L, "textText", user,
                LocalDateTime.now().minusDays(1), null);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void findEverythingWithoutRequestorTest() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findEverythingWithoutRequestor(2L, PageRequest.of(0, 3,
                        Sort.by(Sort.Direction.DESC, "created")));
        equalsItemRequest(itemRequests.get(0), itemRequest2);
    }

    @Test
    void findAllByRequestor_IdTest() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(1L);
        equalsItemRequest(itemRequests.get(0), itemRequest2);
    }

    private void equalsItemRequest(ItemRequest itemRequest, ItemRequest otherItemRequest) {
        assertEquals(itemRequest.getId(), otherItemRequest.getId());
        assertEquals(itemRequest.getDescription(), otherItemRequest.getDescription());
        assertEquals(itemRequest.getCreated(), otherItemRequest.getCreated());
        assertEquals(itemRequest.getRequestor().getId(), otherItemRequest.getRequestor().getId());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}