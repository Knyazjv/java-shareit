package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Post /users, user:{}", userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                           @Validated({Update.class}) @RequestBody UserDto newUserDto) {
        log.info("Patch /users/{}, user:{}", userId, newUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(newUserDto, userId));
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.info("Get /users/{}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Get /users");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Delete /users/{}", userId);
         userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
