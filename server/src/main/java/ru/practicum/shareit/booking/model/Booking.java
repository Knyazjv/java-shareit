package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.WAITING;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;
}
