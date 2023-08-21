package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class MappingUser {
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto userDto) {
        if (userDto.getName().isEmpty()) {
            throw new EmptyException("Имя пользователя не может быть пустым");
        }
        if (userDto.getEmail().isEmpty()) {
            throw new EmptyException("Email не может быть пустым");
        }
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
