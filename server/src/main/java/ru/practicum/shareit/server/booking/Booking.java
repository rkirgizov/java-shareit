package ru.practicum.shareit.server.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.booking.enumeration.Status;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private Status status;

}
