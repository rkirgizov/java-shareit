package ru.practicum.shareit.server.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.enumeration.State;
import ru.practicum.shareit.server.booking.enumeration.Status;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.repo.BookingRepository;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.repo.ItemRepository;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.repo.UserRepository;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static ru.practicum.shareit.server.common.ServerCheckUtility.*;
import static ru.practicum.shareit.server.booking.dto.BookingMapper.*;


@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto) {
        User booker = getUser(userId);
        Item item = getItem(bookingCreateDto.getItemId());
        isItemAvailable(item);
        return toBookingDto(bookingRepository.save(toBooking(bookingCreateDto, booker, item)));
    }

    @Override
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus().equals(Status.WAITING)) {
            isOwner(userId, booking.getItem().getOwner());
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else throw new ValidationException(String.format("Бронирование с ID %S уже подтверждено", bookingId));
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        isBookerOrOwner(userId, booking.getBooker().getId(), booking.getItem().getOwner());
        return toBookingDto(booking);
    }

    @Override
    public Collection<BookingStateRequestDto> findAllBookings(long userId, String requestState) {
        getUser(userId);
        State state = stateConvert(requestState);
        List<BookingStateRequestDto> bookingStateRequestDtoList = new ArrayList<>();
        LocalDateTime requestDateTime = LocalDateTime.now();
        switch (state) {
            case ALL -> {
                bookingStateRequestDtoList = bookingRepository.findByBooker_IdOrderByStartAsc(userId)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }

            case WAITING -> {
                bookingStateRequestDtoList = bookingRepository.findByBooker_IdAndStatusIsOrderByStartAsc(userId,
                                Status.WAITING)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case REJECTED -> {
                bookingStateRequestDtoList = Stream.concat(
                        bookingRepository.findByBooker_IdAndStatusIsOrderByStartAsc(
                                        userId, Status.REJECTED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime)),
                        bookingRepository.findByBooker_IdAndStatusIsOrderByStartAsc(
                                        userId, Status.CANCELED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                ).toList();
            }
            case CURRENT -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByBooker_IdAndStatusIsAndEndIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .filter(bookingStateRequestDto -> bookingStateRequestDto.getStatus().equals(State.CURRENT))
                        .toList();
            }
            case PAST -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByBooker_IdAndStatusIsAndEndIsBeforeOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case FUTURE -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByBooker_IdAndStatusIsAndStartIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
        }
        return bookingStateRequestDtoList;
    }

    @Override
    public Collection<BookingStateRequestDto> findAllOwnerBooking(long userId, String requestState) {
        getUser(userId);
        State state = stateConvert(requestState);
        LocalDateTime requestDateTime = LocalDateTime.now();
        List<BookingStateRequestDto> bookingStateRequestDtoList = new ArrayList<>();
        switch (state) {
            case ALL -> {
                bookingStateRequestDtoList = bookingRepository.findByItemOwnerOrderByStartAsc(userId)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }

            case WAITING -> {
                bookingStateRequestDtoList = bookingRepository.findByItemOwnerAndStatusIsOrderByStartAsc(userId,
                                Status.WAITING)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case REJECTED -> {
                bookingStateRequestDtoList = Stream.concat(
                        bookingRepository.findByItemOwnerAndStatusIsOrderByStartAsc(
                                        userId, Status.REJECTED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime)),
                        bookingRepository.findByItemOwnerAndStatusIsOrderByStartAsc(
                                        userId, Status.CANCELED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                ).toList();

            }
            case CURRENT -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByItemOwnerAndStatusIsAndEndIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .filter(bookingStateRequestDto -> bookingStateRequestDto.getStatus().equals(State.CURRENT))
                        .toList();
            }
            case PAST -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByItemOwnerAndStatusIsAndEndIsBeforeOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case FUTURE -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByItemOwnerAndStatusIsAndStartIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
        }
        return bookingStateRequestDtoList;
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
    }

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
    }

    private State stateConvert(String requestState) {
        State state;
        try {
            state = State.valueOf(requestState.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Параметр запроса статуса state=%s, не верен", requestState));
        }
        return state;
    }
}
