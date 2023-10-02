package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.MappingUser;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MappingUser mappingUser;
    private static final String USER_NOT_FOUND = "Пользователь не найден, id: ";

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        return mappingUser.toDto(userRepository.save(mappingUser.toUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));
        if (!(userDto.getName() == null) && !userDto.getName().isEmpty()) {
            newUser.setName(userDto.getName());
        }
        if (!(userDto.getEmail() == null) && !userDto.getEmail().isEmpty()) {
            newUser.setEmail(userDto.getEmail());
        }
        return mappingUser.toDto(userRepository.save(newUser));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));
        return mappingUser.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(mappingUser::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));
        userRepository.deleteById(userId);
    }
}