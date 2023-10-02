package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.MappingUser;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.controller.UserControllerTest.equalsUserDto;


class UserServiceTest {

    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository, mappingUser);
    }

    private final MappingUser mappingUser = new MappingUser();
    private final User user = new User(1L, "user", "mail@mail.ru");
    private final UserDto userDto = new UserDto(1L, "user", "mail@mail.ru");
    private final Long userId = 1L;
    private static final String USER_NOT_FOUND = "Пользователь не найден, id: ";

    @Test
    void createUserTest() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userDtoResponse = userService.createUser(userDto);
        equalsUserDto(userDto, userDtoResponse);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUserTest() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userDtoResponse = userService.updateUser(userDto, userId);
        equalsUserDto(userDto, userDtoResponse);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Exception e = assertThrows(NotFoundException.class, () -> userService.updateUser(userDto, userId));
        assertEquals(USER_NOT_FOUND + userId, e.getMessage());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void updateUser_whenUserNameAndEmailIsNull() {
        UserDto newUser = new UserDto(null, null, null);
        when(userRepository.save(any())).thenReturn(user);

        UserDto userDtoResponse = userService.createUser(newUser);
        equalsUserDto(userDto, userDtoResponse);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser_whenUserNameAndEmailIsEmpty() {
        UserDto newUser = new UserDto(null, "", "");
        when(userRepository.save(any())).thenReturn(user);

        UserDto userDtoResponse = userService.createUser(newUser);
        equalsUserDto(userDto, userDtoResponse);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        User userResponse = userService.getUserById(userId);
        equalsUserDto(userDto, mappingUser.toDto(userResponse));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void getUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        assertEquals(USER_NOT_FOUND + userId, e.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void getUserDtoByIdTest() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userDtoResponse = userService.getUserDtoById(userId);
        equalsUserDto(userDto, userDtoResponse);
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void getUserDtoById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Exception e = assertThrows(NotFoundException.class, () -> userService.getUserDtoById(userId));
        assertEquals(e.getMessage(), USER_NOT_FOUND + userId);
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void getAllUsersTest() {
        User user2 = new User(2L, "user2", "mail@yandex.ru");
        List<User> users = List.of(user, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.getAllUsers();
        assertEquals(users.size(), userDtos.size());
        equalsUserDto(userDto, userDtos.get(0));
        equalsUserDto(mappingUser.toDto(user2), userDtos.get(1));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    void deleteUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Exception e = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        assertEquals(USER_NOT_FOUND + userId, e.getMessage());
        verify(userRepository, times(0)).deleteById(any());
    }
}