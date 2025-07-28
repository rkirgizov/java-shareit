package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.BookingClient; // Импорт вашего клиента
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor // Lombok создаст конструктор
public class BookingController {

    private final BookingClient bookingClient; // Инжектим клиента

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestBody BookingDto bookingDto) {
        // Делегируем вызов клиенту
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingClient.getAllBookingsByBooker(bookerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingClient.getAllBookingsByOwner(ownerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }
}