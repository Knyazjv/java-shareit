package ru.practicum.shareit.user.repository.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> userMap = new HashMap<>();
    private Long userId = 1L;

    @Override
    public User createUser(User user) {
        user.setId(userId);
        userMap.put(userId, user);
        return userMap.get(userId++);
    }

    @Override
    public User updateUser(User user) {
        userMap.put(user.getId(), user);
        return userMap.get(user.getId());
    }

    @Override
    public User getUserById(Long userId) {
        if (userMap.containsKey(userId)) {
            return userMap.get(userId);
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void deleteUser(Long userId) {
        userMap.remove(userId);
    }

    @Override
    public boolean checkEmailUser(String email, Long userId) {
        List<User> users = userMap.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .collect(Collectors.toList());
        if (users.size() > 2) {
            return true;
        }
        if (users.size() == 1) {
            return !users.get(0).getId().equals(userId);
        }
        return false;
    }

    @Override
    public boolean userIsExist(Long userId) {
        return userMap.containsKey(userId);
    }
}
