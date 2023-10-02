package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long userId, Sort sort, PageRequest pageRequest);

    @Query("select bn " +
            "from Booking as bn " +
            "where bn.booker.id = ?1 " +
            "and bn.start < ?2 and bn.end > ?2 " +
            "order by bn.start desc ")
    List<Booking> findAllByBookerIdDataBetween(Long userId, LocalDateTime localDateTime,
                                               Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime localDateTime,
                                                Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime localDateTime,
                                                 Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus bookingStatus,
                                             Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_User_Id(Long userId, Sort sort, PageRequest pageRequest);

    @Query("select bn " +
            "from Booking as bn " +
            "where bn.item.user.id = ?1 " +
            "and bn.start < ?2 and bn.end > ?2 " +
            "order by bn.start desc ")
    List<Booking> findAllByItemUserIdDataBetween(Long userId, LocalDateTime localDateTime,
                                                 Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_User_IdAndEndBefore(Long userId, LocalDateTime localDateTime,
                                                    Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_User_IdAndStartAfter(Long userId, LocalDateTime localDateTime,
                                                     Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_User_IdAndStatus(Long userId, BookingStatus bookingStatus,
                                                 Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndItem_IdAndStatusAndStartBefore(Long userId, Long itemId,
                                                                BookingStatus status, LocalDateTime dateTime);

    List<Booking> findAllByItem_IdAndItem_User_Id(Long itemId, Long userId, Sort sort);
}
