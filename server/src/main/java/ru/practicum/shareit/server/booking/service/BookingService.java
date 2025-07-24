package ru.practicum.shareit.server.booking.service;

import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    Collection<BookingStateRequestDto> findAllBookings(long userId, String state);

    Collection<BookingStateRequestDto> findAllOwnerBooking(long userId, String state);
}
