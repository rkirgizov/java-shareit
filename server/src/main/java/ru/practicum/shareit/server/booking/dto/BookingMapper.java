package ru.practicum.shareit.server.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.enumeration.State;
import ru.practicum.shareit.server.booking.enumeration.Status;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.server.item.dto.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.server.user.dto.UserMapper.toUserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(BookingCreateDto bookingCreateDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(toItemDto(booking.getItem()))
                .booker(toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingStateRequestDto toBookingStateRequestDto(Booking booking, LocalDateTime requestDatetime) {

        BookingStateRequestDto bookingStateRequestDto = BookingStateRequestDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(toItemDto(booking.getItem()))
                .booker(toUserDto(booking.getBooker()))
                .build();

        if (booking.getStatus().equals(Status.CANCELED) || booking.getStatus().equals(Status.REJECTED))
            bookingStateRequestDto.setStatus(State.REJECTED);
        if (booking.getStatus().equals(Status.APPROVED) && (booking.getStart().isAfter(requestDatetime))) {
            bookingStateRequestDto.setStatus(State.FUTURE);
            return bookingStateRequestDto;
        }
        if (booking.getStatus().equals(Status.APPROVED) && (booking.getEnd().isBefore(requestDatetime))) {
            bookingStateRequestDto.setStatus(State.PAST);
            return bookingStateRequestDto;
        }

        if (booking.getStatus().equals(Status.APPROVED) &&
                ((booking.getStart().isEqual(requestDatetime)
                        || booking.getStart().isBefore(requestDatetime))
                        || (booking.getEnd().isEqual(requestDatetime)
                        || booking.getEnd().isAfter(requestDatetime))))
            bookingStateRequestDto.setStatus(State.CURRENT);

        return bookingStateRequestDto;
    }

}
