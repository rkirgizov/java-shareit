package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;


@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(org.springframework.http.client.SimpleClientHttpRequestFactory::new) // <<< ИЗМЕНЕНО ЗДЕСЬ
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(long bookerId, BookingDto bookingDto) {
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByBooker(long bookerId) {
        return get("", bookerId);
    }

    public ResponseEntity<Object> getAllBookingsByOwner(long ownerId) {
        return get("/owner", ownerId);
    }

    public ResponseEntity<Object> approveBooking(long ownerId, long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId, null);
    }
}
