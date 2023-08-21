package ru.practicum.shareit.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.MappingUser;
import ru.practicum.shareit.user.service.UserService;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MappingUser mappingUser;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MappingUser mappingUser) {
        this.userRepository = userRepository;
        this.mappingUser = mappingUser;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.checkEmailUser(userDto.getEmail(), null)) {
            throw new ValidationException("Электронная почта уже используется, email: " + userDto.getEmail());
        }
        return mappingUser.toDto(userRepository.createUser(mappingUser.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) throws IllegalAccessException {
        if (userRepository.checkEmailUser(userDto.getEmail(), userId)) {
            throw new ValidationException("Электронная почта уже используется, email: " + userDto.getEmail());
        }
        UserDto newUser = applyUpdateToUser(userDto, getUserById(userId));
        return mappingUser.toDto(userRepository.updateUser(mappingUser.toUser(newUser)));
    }

    @Override
    public UserDto getUserById(Long userId) {
        checkUserIsExist(userId);
        return mappingUser.toDto(userRepository.getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(mappingUser::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        checkUserIsExist(userId);
        userRepository.deleteUser(userId);
    }

    @Override
    public void checkUserIsExist(Long userId) {
        if (!userRepository.userIsExist(userId)) {
            throw new NotFoundException("Пользователь не найден, id: " + userId);
        }
    }

    private UserDto applyUpdateToUser(UserDto newUser, UserDto user) throws IllegalAccessException {
        Field[] fields = UserDto.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(newUser) == null) {
                field.set(newUser, field.get(user));
            }
        }
        return newUser;
    }
}