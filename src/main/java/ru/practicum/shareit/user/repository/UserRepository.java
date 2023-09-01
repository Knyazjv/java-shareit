package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void deleteUser(Long userId);

    boolean checkEmailUser(String email, Long userId);

    boolean userIsExist(Long userId);
}
