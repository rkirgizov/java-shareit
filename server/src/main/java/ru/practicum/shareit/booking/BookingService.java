package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long bookerId, BookingDto bookingDto);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsByBooker(Long bookerId);

    List<BookingDto> getAllBookingsByOwner(Long ownerId);

    BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved);

    List<BookingDto> getBookingsByUser(Long userId, String state, int from, int size);

    List<BookingDto> getBookingsByOwner(Long ownerId, String state, int from, int size);
}
