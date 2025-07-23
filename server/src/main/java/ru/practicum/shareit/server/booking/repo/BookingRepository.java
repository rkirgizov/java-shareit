package ru.practicum.shareit.server.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.booking.enumeration.Status;
import ru.practicum.shareit.server.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartAsc(long bookerId);

    List<Booking> findByBooker_IdAndStatusIsOrderByStartAsc(long bookerId, Status status);

    List<Booking> findByBooker_IdAndStatusIsAndEndIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                         LocalDateTime requestDateTime);
    List<Booking> findByBooker_IdAndStatusIsAndEndIsBeforeOrderByStartAsc(long bookerId, Status status,
                                                                          LocalDateTime requestDateTime);
    List<Booking> findByBooker_IdAndStatusIsAndStartIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                           LocalDateTime requestDateTime);
    List<Booking> findByItemOwnerOrderByStartAsc(long bookerId);

    List<Booking> findByItemOwnerAndStatusIsOrderByStartAsc(long bookerId, Status status);

    List<Booking> findByItemOwnerAndStatusIsAndEndIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                         LocalDateTime requestDateTime);
    List<Booking> findByItemOwnerAndStatusIsAndEndIsBeforeOrderByStartAsc(long bookerId, Status status,
                                                                          LocalDateTime requestDateTime);

    List<Booking> findByItemOwnerAndStatusIsAndStartIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                           LocalDateTime requestDateTime);

    List<Booking> findByItem_IdAndEndIsBeforeOrderByEndAsc(Long itemId, LocalDateTime end);

    List<Booking> findByItem_IdAndStartIsAfterOrderByEndAsc(Long itemId, LocalDateTime start);

    Optional<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);

    List<Booking> findByItem_IdInOrderByStartAsc(List<Long> itemList);
}
