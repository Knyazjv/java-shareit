package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user, Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void deleteUser(Long userId);

    void checkUserIsExist(Long userId);
}
