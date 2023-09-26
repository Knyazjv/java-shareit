package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingState;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceImplIT {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void getAllBookingsByStateWithPaginationTest() {
        Long userId = 1L;
        UserDto userDto1 = new UserDto(1L, "user", "mail@mail.ru");
        UserDto userDto2 = new UserDto(2L, "name", "ya@mail.ru");
        ItemDto itemDto1 = new ItemDto(1L, "3name2", "2description2ITEm", true, null);

        LocalDateTime dateTimeStart = LocalDateTime.now().plusDays(1);
        LocalDateTime dateTimeEnd = LocalDateTime.now().plusDays(10);

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(1L, dateTimeStart, dateTimeEnd, itemDto1.getId());

        userService.createUser(userDto1);
        userService.createUser(userDto2);
        itemService.createItem(itemDto1, userId);
        bookingService.createBooking(userDto2.getId(), bookingDtoRequest);

        List<BookingDtoResponse> bookingDtoResponseList = bookingService
                .getAllBookingsByStateWithPagination(userDto2.getId(), BookingState.ALL, PageRequest.of(0, 10));
        BookingDtoResponse response = bookingDtoResponseList.get(0);

        assertEquals(bookingDtoRequest.getId(), response.getId());
        assertEquals(BookingStatus.WAITING, response.getStatus());
        assertEquals(itemDto1.getId(), response.getItem().getId());
        assertEquals(userDto2.getId(), response.getBooker().getId());
        assertEquals(bookingDtoRequest.getStart(), response.getStart());
        assertEquals(bookingDtoRequest.getEnd(), response.getEnd());
    }
}