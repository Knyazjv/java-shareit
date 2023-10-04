package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplIT {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private final Long userId = 1L;

    @Test
    void createItemRequestTest() {
        UserDto userDto = new UserDto(null, "user", "mail@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto("textText");
        userService.createUser(userDto);
        itemRequestService.createItemRequest(itemRequestDto, userId);

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", userId).getSingleResult();

        assertEquals(1L, itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    void createItemRequest_whenUserNotfound() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("textText");
        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, userId));
        assertEquals("Пользователь не найден, id: " + userId, exception.getMessage());
    }
}