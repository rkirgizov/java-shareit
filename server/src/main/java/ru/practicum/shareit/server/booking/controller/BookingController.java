package ru.practicum.shareit.server.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;

import java.util.Collection;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody BookingCreateDto bookingCreateDto) {

        return bookingService.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable("bookingId") long bookingId,
                             @RequestParam(name = "approved") boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingStateRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", required = false,
                                                              defaultValue = "ALL") String state) {
        return bookingService.findAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingStateRequestDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam(name = "state", required = false,
                                                                     defaultValue = "ALL") String state) {
        return bookingService.findAllOwnerBooking(userId, state);
    }

}
