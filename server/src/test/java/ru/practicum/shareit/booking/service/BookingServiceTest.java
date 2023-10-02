package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingState;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    BookingRepository bookingRepository;
    UserService userService;
    ItemService itemService;
    MappingBooking mappingBooking = new MappingBooking();
    BookingService bookingService;

    Long userId = 1L;
    User user1 = new User(1L, "user", "mail@mail.ru");
    UserDto userDto1 = new UserDto(1L, "user", "mail@mail.ru");
    User user2 = new User(2L, "name", "ya@mail.ru");
    User user3 = new User(3L, "3name3", "yandex@mail.ru");
    Item item2 = new Item(2L, user2, "3name2", "2description2ITEm", true, null);
    ItemDto itemDto2 = new ItemDto(2L, "3name2", "2description2ITEm", true, null);

    LocalDateTime dateTimeStart = LocalDateTime.now().plusDays(1);
    LocalDateTime dateTimeEnd = LocalDateTime.now().plusDays(10);

    Booking booking1 = new Booking(1L, user1,  item2, BookingStatus.WAITING, dateTimeStart, dateTimeEnd);
    BookingDtoResponse bookingDtoResponse1 = new BookingDtoResponse(1L, userDto1, itemDto2, BookingStatus.WAITING,
            dateTimeStart, dateTimeEnd);
    BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(null, dateTimeStart, dateTimeEnd, item2.getId());

    @BeforeEach
    void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        userService = mock(UserService.class);
        itemService = mock(ItemService.class);
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService, mappingBooking);
    }

    @Test
    void createBookingTest() {
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemService.getItemById(any())).thenReturn(item2);
        when(bookingRepository.save(any())).thenReturn(booking1);

        BookingDtoResponse response = bookingService.createBooking(userId, bookingDtoRequest);
        equalsBookingDtoResponse(bookingDtoResponse1, response);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);
        when(itemService.getItemById(any())).thenReturn(item2);
        when(bookingRepository.save(any())).thenReturn(booking1);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void createBooking_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemService.getItemById(any())).thenThrow(NotFoundException.class);
        when(bookingRepository.save(any())).thenReturn(booking1);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void createBooking_whenItemAvailableIsFalse_thenBookingExceptionThrown() {
        Item item = new Item(1L, user1, "1iTem1", "1description1", false, null);
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemService.getItemById(any())).thenReturn(item);
        when(bookingRepository.save(any())).thenReturn(booking1);

        assertThrows(BookingException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void createBooking_whenBookerIsOwner_thenNotFoundExceptionThrown() {
        Item item = new Item(1L, user1, "1iTem1", "1description1", true, null);
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemService.getItemById(any())).thenReturn(item);
        when(bookingRepository.save(any())).thenReturn(booking1);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void confirmationBookingTest() {
        Booking booking = new Booking(1L, user1,  item2, BookingStatus.APPROVED, dateTimeStart, dateTimeEnd);
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(1L, userDto1, itemDto2, BookingStatus.APPROVED,
                dateTimeStart, dateTimeEnd);

        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoResponse response = bookingService.confirmationBooking(user2.getId(), item2.getId(), true);
        equalsBookingDtoResponse(bookingDtoResponse, response);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void confirmationBooking_whenUserIsNotOwner_thenNotFoundExceptionThrown() {
        Booking booking = new Booking(1L, user1,  item2, BookingStatus.APPROVED, dateTimeStart, dateTimeEnd);

        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService
                .confirmationBooking(user1.getId(), item2.getId(), true));

        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void confirmationBooking_whenBookingStatusApproved_thenBookingExceptionThrown() {
        Booking booking = new Booking(1L, user1,  item2, BookingStatus.APPROVED, dateTimeStart, dateTimeEnd);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(BookingException.class, () -> bookingService
                .confirmationBooking(user2.getId(), item2.getId(), true));

        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void confirmationBooking_whenBookingStatusRejected_thenBookingExceptionThrown() {
        Booking booking = new Booking(1L, user1,  item2, BookingStatus.REJECTED, dateTimeStart, dateTimeEnd);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(BookingException.class, () -> bookingService
                .confirmationBooking(user2.getId(), item2.getId(), true));

        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void confirmationBooking_whenApprovedIsNull_thenBookingExceptionThrown() {
        Booking booking = new Booking(1L, user1,  item2, BookingStatus.WAITING, dateTimeStart, dateTimeEnd);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(BookingException.class, () -> bookingService
                .confirmationBooking(user2.getId(), item2.getId(), null));

        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void confirmationBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        Booking booking = new Booking(1L, user1,  item2, BookingStatus.WAITING, dateTimeStart, dateTimeEnd);

        when(userService.getUserDtoById(any())).thenThrow(NotFoundException.class);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService
                .confirmationBooking(user2.getId(), item2.getId(), true));

        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void confirmationBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        Booking booking = new Booking(1L, user1,  item2, BookingStatus.WAITING, dateTimeStart, dateTimeEnd);

        when(bookingRepository.findById(any())).thenThrow(NotFoundException.class);
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService
                .confirmationBooking(user2.getId(), item2.getId(), true));

        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));

        BookingDtoResponse response = bookingService.getBookingById(user2.getId(), booking1.getId());
        equalsBookingDtoResponse(bookingDtoResponse1, response);
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingById_whenBookingNotFound_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService
                .getBookingById(user2.getId(), booking1.getId()));

        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingById_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserDtoById(any())).thenThrow(NotFoundException.class);
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));

        assertThrows(NotFoundException.class, () -> bookingService
                .getBookingById(user2.getId(), booking1.getId()));

        verify(bookingRepository, times(0)).findById(any());
    }

    @Test
    void getBookingById_whenUserIsNotBookerOrOwner_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));

        assertThrows(NotFoundException.class, () -> bookingService
                .getBookingById(user3.getId(), booking1.getId()));

        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getAllBookingsByStateWithPagination_whenStateAll() {
        when(bookingRepository.findAllByBookerId(any(), any(), any())).thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllBookingsByStateWithPagination(userId, BookingState.ALL, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1)).findAllByBookerId(any(), any(), any());
    }

    @Test
    void getAllBookingsByStateWithPagination_whenStateCurrent() {
        when(bookingRepository.findAllByBookerIdDataBetween(any(), any(), any(), any())).thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllBookingsByStateWithPagination(userId, BookingState.CURRENT, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByBookerIdDataBetween(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsByStateWithPagination_whenStatePast() {
        when(bookingRepository.findAllByBookerIdAndEndBefore(any(), any(), any(), any())).thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllBookingsByStateWithPagination(userId, BookingState.PAST, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBefore(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsByStateWithPagination_whenStateFuture() {
        when(bookingRepository.findAllByBookerIdAndStartAfter(any(), any(), any(), any())).thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllBookingsByStateWithPagination(userId, BookingState.FUTURE, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfter(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsByStateWithPagination_whenStateWaiting() {
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any(), any())).thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllBookingsByStateWithPagination(userId, BookingState.WAITING, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsByStateWithPagination_whenStateRejected() {
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any(), any())).thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllBookingsByStateWithPagination(userId, BookingState.REJECTED, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsByStateWithPagination_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserDtoById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService
                .getAllBookingsByStateWithPagination(userId, BookingState.ALL, PageRequest.of(0, 10)));
        verify(bookingRepository, times(0)).findAllByBookerId(any(), any(), any());
    }

    @Test
    void getAllOwnerBookingsWithPagination_whenStateAll() {
        when(bookingRepository.findAllByItem_User_Id(any(), any(), any())).thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllOwnerBookingsWithPagination(userId, BookingState.ALL, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1)).findAllByItem_User_Id(any(), any(), any());
    }

    @Test
    void getAllOwnerBookingsWithPagination_whenStateCurrent() {
        when(bookingRepository.findAllByItemUserIdDataBetween(any(), any(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllOwnerBookingsWithPagination(userId, BookingState.CURRENT, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByItemUserIdDataBetween(any(), any(), any(), any());
    }

    @Test
    void getAllOwnerBookingsWithPagination_whenStatePast() {
        when(bookingRepository.findAllByItem_User_IdAndEndBefore(any(),any(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllOwnerBookingsWithPagination(userId, BookingState.PAST, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByItem_User_IdAndEndBefore(any(),any(), any(), any());
    }

    @Test
    void getAllOwnerBookingsWithPagination_whenStateFuture() {
        when(bookingRepository.findAllByItem_User_IdAndStartAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllOwnerBookingsWithPagination(userId, BookingState.FUTURE, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByItem_User_IdAndStartAfter(any(), any(), any(), any());
    }

    @Test
    void getAllOwnerBookingsWithPagination_whenStateWaiting() {
        when(bookingRepository.findAllByItem_User_IdAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllOwnerBookingsWithPagination(userId, BookingState.WAITING, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByItem_User_IdAndStatus(any(), any(), any(), any());
    }

    @Test
    void getAllOwnerBookingsWithPagination_whenStateRejected() {
        when(bookingRepository.findAllByItem_User_IdAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> responses = bookingService
                .getAllOwnerBookingsWithPagination(userId, BookingState.REJECTED, PageRequest.of(0, 10));
        equalsBookingDtoResponse(bookingDtoResponse1, responses.get(0));
        verify(bookingRepository, times(1))
                .findAllByItem_User_IdAndStatus(any(), any(), any(), any());
    }

    @Test
    void getAllOwnerBookingsWithPagination_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserDtoById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService
                .getAllOwnerBookingsWithPagination(userId, BookingState.ALL, PageRequest.of(0, 10)));
        verify(bookingRepository, times(0)).findAllByItem_User_Id(any(), any(), any());
    }

    private void equalsBookingDtoResponse(BookingDtoResponse bookingDto, BookingDtoResponse otherBookingDto) {
        assertEquals(bookingDto.getId(), otherBookingDto.getId());
        assertEquals(bookingDto.getStatus(), otherBookingDto.getStatus());
        assertEquals(bookingDto.getItem().getId(), otherBookingDto.getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), otherBookingDto.getBooker().getId());
        assertEquals(bookingDto.getStart(), otherBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), otherBookingDto.getEnd());
    }
}