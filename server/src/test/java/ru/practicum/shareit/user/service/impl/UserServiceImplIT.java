package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplIT {

    private final EntityManager em;
    private final UserServiceImpl userService;

    private final Long userId = 1L;

    @Test
    void createUserTest() {
        UserDto userDto = new UserDto(null, "user", "mail@mail.ru");
        userService.createUser(userDto);
        userDto.setId(userId);
        TypedQuery<User> query = em.createQuery("Select i from User i where i.id = :id", User.class);
        User user = query.setParameter("id", userId).getSingleResult();

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void updateUserTest() {
        UserDto userDto = new UserDto(null, "user", "mail@mail.ru");
        userService.createUser(userDto);
        userDto.setName("updateName");
        userService.updateUser(userDto, userId);
        userDto.setId(userId);
        TypedQuery<User> query = em.createQuery("Select i from User i where i.id = :id", User.class);
        User user = query.setParameter("id", userId).getSingleResult();

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void getUserDtoByIdTest() {
        UserDto userDto = new UserDto(null, "user", "mail@mail.ru");
        userService.createUser(userDto);
        userDto.setId(userId);
        UserDto userDtoResponse = userService.getUserDtoById(userId);

        assertEquals(userDto.getId(), userDtoResponse.getId());
        assertEquals(userDto.getName(), userDtoResponse.getName());
        assertEquals(userDto.getEmail(), userDtoResponse.getEmail());
    }
}