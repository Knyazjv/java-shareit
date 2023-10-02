package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerIT {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;


    @SneakyThrows
    @Test
    void createUserTest() {
        UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");
        when(userService.createUser(any())).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userDto), result);
        verify(userService, times(1)).createUser(any());
    }

    @SneakyThrows
    @Test
    void createUser_whenUserIsNotValid_thenReturnedStatusBadRequest() {
        UserDto userDto = new UserDto(null, null, "mail@mail.ru");
        when(userService.createUser(any())).thenReturn(userDto);

        performMVCCreateUser(userDto);

        userDto.setName("name");
        userDto.setEmail(null);
        performMVCCreateUser(userDto);;

        userDto.setEmail("yandex");
        performMVCCreateUser(userDto);

        verify(userService, never()).createUser(any());
    }

    private void performMVCCreateUser(UserDto userDto) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUserTest() {
        UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");
        when(userService.updateUser(any(), any())).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userDto), result);
        verify(userService, times(1)).updateUser(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserByIdTest() {
        UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");
        when(userService.getUserDtoById(any())).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userDto), result);
        verify(userService, times(1)).getUserDtoById(any());
    }

    @SneakyThrows
    @Test
    void getAllUsersTest() {
        UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");
        UserDto userDto2 = new UserDto(2L, "name2", "email@ya.ru");
        List<UserDto> userDtos = List.of(userDto, userDto2);
        when(userService.getAllUsers()).thenReturn(userDtos);

        String result = mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userDtos), result);
        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void deleteUserTest() {
        String result = mockMvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("", result);
        verify(userService, times(1)).deleteUser(any());
    }
}