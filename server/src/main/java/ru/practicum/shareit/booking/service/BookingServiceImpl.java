package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enumBooking.BookingState;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final MappingBooking mappingBooking;
    private static final String BOOKING_NOT_FOUND = "Бронирование не найдено, bookingId: ";


    @Transactional
    @Override
    public BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingDtoRequest.getItemId());
        if (!item.getAvailable()) {
            throw new BookingException("Вещь не доступна для бронирования");
        }
        if (item.getUser().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }
        return mappingBooking.toBookingDtoResponse(bookingRepository.save(mappingBooking.toBooking(null,
                user, item,
                BookingStatus.WAITING, bookingDtoRequest)));
    }

    @Transactional
    @Override
    public BookingDtoResponse confirmationBooking(Long userId, Long bookingId, Boolean approved) {
        userService.getUserDtoById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND + bookingId));
        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new NotFoundException("Подтвердить бронирование может только хозяин вещи");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) || booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new BookingException("Изменить статус бронирования нельзя");
        }
        if (approved == null) {
            throw new BookingException("Отсутсвует согласие на бронирование");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return mappingBooking.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoResponse getBookingById(Long userId, Long bookingId) {
        userService.getUserDtoById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND + bookingId));
        boolean isOwner = booking.getItem().getUser().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);
        if (!isOwner && !isBooker) {
            throw new NotFoundException("Посмотреть бронирование может только Owner или Booker");
        }
        return mappingBooking.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getAllBookingsByStateWithPagination(Long userId,
                                                                        BookingState state, PageRequest pageRequest) {
        userService.getUserDtoById(userId);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by("start").descending();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, sort, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdDataBetween(userId, dateTime, sort, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, dateTime, sort, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, dateTime, sort, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId,
                        BookingStatus.WAITING, sort, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId,
                        BookingStatus.REJECTED, sort, pageRequest);
                break;
        }
        return bookings.stream()
                .map(mappingBooking::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getAllOwnerBookingsWithPagination(Long userId,
                                                                      BookingState state, PageRequest pageRequest) {
        userService.getUserDtoById(userId);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by("start").descending();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_User_Id(userId, sort, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemUserIdDataBetween(userId, dateTime, sort, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_User_IdAndEndBefore(userId, dateTime, sort, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_User_IdAndStartAfter(userId, dateTime, sort, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_User_IdAndStatus(userId,
                        BookingStatus.WAITING, sort, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_User_IdAndStatus(userId,
                        BookingStatus.REJECTED, sort, pageRequest);
                break;
        }
        return bookings.stream()
                .map(mappingBooking::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}
