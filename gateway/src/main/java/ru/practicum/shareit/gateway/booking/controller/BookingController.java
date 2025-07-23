package ru.practicum.shareit.gateway.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.gateway.booking.BookingClient;
import ru.practicum.shareit.gateway.booking.enumeration.ApproveState;
import ru.practicum.shareit.gateway.booking.enumeration.BookingState;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid NewBookingDto newBookingDto) {
        log.info("Creating booking {}, userId={}", newBookingDto, userId);
        return bookingClient.createBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Positive @PathVariable("bookingId") long bookingId,
                                         @RequestParam(name = "approved") String approved) {
        ApproveState approveState = ApproveState.from(approved)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный логический параметр "
                        + approved));
        log.info("Ответ владельца {} на подтверждение {} бронирования  {}", userId, approved, bookingId);
        return bookingClient.updateBooking(userId, bookingId, approveState);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(name = "state", defaultValue = "all",
                                                  required = false) String stateParam,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state", defaultValue = "all",
                                                         required = false) String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from",
                                                         defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size",
                                                         defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllBookingByOwner(userId, state, from, size);
    }

}
