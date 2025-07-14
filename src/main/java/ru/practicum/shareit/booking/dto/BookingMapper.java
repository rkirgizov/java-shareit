package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.User;


public class BookingMapper {
    public static BookingResponseDto toBookingDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toResponseDto(booking.getItem()),
                UserMapper.toResponseDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingResponseDto bookingResponseDto, Item item, User broker) {
        return new Booking(
                bookingResponseDto.getId(),
                bookingResponseDto.getStart(),
                bookingResponseDto.getEnd(),
                item,
                broker,
                bookingResponseDto.getStatus()
        );
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User broker) {
        return new Booking(
                null,
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                item,
                broker,
                Status.WAITING);
    }
}