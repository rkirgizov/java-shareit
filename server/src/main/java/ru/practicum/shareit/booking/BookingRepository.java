package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                          LocalDateTime nowStart,
                                                                          LocalDateTime nowEnd,
                                                                          Pageable pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId,
                                                             LocalDateTime now,
                                                             Pageable pageable);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId,
                                                              LocalDateTime now,
                                                              Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId,
                                                          BookingStatus status,
                                                          Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                             LocalDateTime nowStart,
                                                                             LocalDateTime nowEnd,
                                                                             Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId,
                                                                LocalDateTime now,
                                                                Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId,
                                                                 LocalDateTime now,
                                                                 Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId,
                                                             BookingStatus status,
                                                             Pageable pageable);
    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);

    List<Booking> findByItemIdAndEndBeforeOrderByStartDesc(Long itemId, LocalDateTime end);

    List<Booking> findByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);


}
