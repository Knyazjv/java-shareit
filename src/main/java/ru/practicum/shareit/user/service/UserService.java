package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserDtoById(Long userId);

    User getUserById(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);
}