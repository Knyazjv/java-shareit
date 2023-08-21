package ru.practicum.shareit.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.checkEmailUser(user.getEmail(), null)) {
            throw new ValidationException("Электронная почта уже используется, email: " + user.getEmail());
        }
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(@Valid User user, Long userId) {
        if (userRepository.checkEmailUser(user.getEmail(), userId)) {
            throw new ValidationException("Электронная почта уже используется, email: " + user.getEmail());
        }
        return userRepository.updateUser(user);
    }

    @Override
    public User getUserById(Long userId) {
        checkUserIsExist(userId);
        return userRepository.getUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
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
}