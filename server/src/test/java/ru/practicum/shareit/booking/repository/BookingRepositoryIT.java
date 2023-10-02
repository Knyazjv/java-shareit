package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user1 = new User(1L, "user", "mail@mail.ru");
    User user2 = new User(2L, "name", "ya@mail.ru");
    User user3 = new User(3L, "3name3", "yandex@mail.ru");
    Item item1 = new Item(1L, user1, "1iTem1", "1description1", true, null);
    Item item2 = new Item(2L, user3, "3name2", "2description2ITEm", true, null);
    Booking booking1 = new Booking(1L, user2, item1, BookingStatus.WAITING,
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(10));
    Booking booking2 = new Booking(2L, user2, item2, BookingStatus.APPROVED,
            LocalDateTime.now().minusDays(6), LocalDateTime.now().plusDays(10));
    Booking booking3 = new Booking(3L, user2, item2, BookingStatus.APPROVED,
            LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1));
    Booking booking4 = new Booking(4L, user2, item1, BookingStatus.REJECTED,
            LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(10));
    Booking booking5 = new Booking(5L, user2, item2, BookingStatus.WAITING,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10));
    Sort sort = Sort.by("start").descending();

    @BeforeEach
    void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
        bookingRepository.save(booking5);
    }

    @Test
    void findAllByBookerIdDataBetweenTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdDataBetween(2L, LocalDateTime.now(), sort,
                PageRequest.of(0, 10));
        equalsBooking(booking1, bookings.get(0));
        equalsBooking(booking2, bookings.get(1));
        equalsBooking(booking4, bookings.get(2));
    }


    @Test
    void findAllByItemUserIdDataBetweenTest() {
        List<Booking> bookings = bookingRepository.findAllByItemUserIdDataBetween(1L, LocalDateTime.now(), sort,
                PageRequest.of(0, 10));
        equalsBooking(booking1, bookings.get(0));
        equalsBooking(booking4, bookings.get(1));
    }

    private void equalsBooking(Booking booking, Booking otherBooking) {
        assertEquals(booking.getId(), otherBooking.getId());
        assertEquals(booking.getBooker().getId(), otherBooking.getBooker().getId());
        assertEquals(booking.getItem().getId(), otherBooking.getItem().getId());
        assertEquals(booking.getStatus(), otherBooking.getStatus());
        assertEquals(booking.getStart(), otherBooking.getStart());
        assertEquals(booking.getEnd(), otherBooking.getEnd());

    }

}