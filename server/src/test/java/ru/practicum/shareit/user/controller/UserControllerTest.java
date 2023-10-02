package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");
    UserDto emptyUserDto = new UserDto(null, null, null);

    @Test
    void createUser_whenInvoked_thenResponseStatusCreatedWithUserDtoInBody() {
        when(userService.createUser(any())).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.createUser(emptyUserDto);
        UserDto userDtoResponse = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert userDtoResponse != null;
        equalsUserDto(userDto, userDtoResponse);
    }

    @Test
    void updateUser_whenInvoked_thenResponseStatusOkWithUserDtoInBody() {
        when(userService.updateUser(any(), any())).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.updateUser(1L, emptyUserDto);
        UserDto userDtoResponse = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert userDtoResponse != null;
        equalsUserDto(userDto, userDtoResponse);
    }

    @Test
    void getUserById_whenInvoked_thenResponseStatusOkWithUserDtoInBody() {
        when(userService.getUserDtoById(any())).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getUserById(1L);
        UserDto userDtoResponse = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert userDtoResponse != null;
        equalsUserDto(userDto, userDtoResponse);
    }

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUserDtoListInBody() {
        UserDto userDto2 = new UserDto(2L, "name2", "email@ya.ru");
        List<UserDto> userDtos = List.of(userDto, userDto2);
        when(userService.getAllUsers()).thenReturn(userDtos);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDtos.size(), Objects.requireNonNull(response.getBody()).size());
        equalsUserDto(userDto, response.getBody().get(0));
        equalsUserDto(userDto2, response.getBody().get(1));
    }

    @Test
    void deleteUser_whenInvoked_thenResponseStatusOk() {
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public static void equalsUserDto(UserDto userDto, UserDto userDtoResponse) {
        assertEquals(userDto.getId(), userDtoResponse.getId());
        assertEquals(userDto.getName(), userDtoResponse.getName());
        assertEquals(userDto.getEmail(), userDtoResponse.getEmail());
    }
}