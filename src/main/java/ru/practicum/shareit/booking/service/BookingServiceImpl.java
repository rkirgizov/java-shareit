package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enumeration.State;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {

        if (!bookingCreateDto.getEnd().isAfter(bookingCreateDto.getStart())) {
            throw new BadRequestException("End time must be after start time");
        }

        User booker = getUserOrThrow(userId);
        Item item = getItemOrThrow(bookingCreateDto.getItemId());
        if (!item.getAvailable()) {
            throw new BadRequestException("Item " + item.getId() + " is not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("User " + userId + " is owner of item " + item.getId());
        }
        Booking booking = BookingMapper.toBooking(bookingCreateDto, item, booker);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(saved);

    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);
        if ((booking.getStatus() != Status.WAITING)) {
            throw new ConflictException("Booking " + bookingId + " is not in waiting state");
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("User " + userId + " is not the owner of item " + booking.getItem().getId());
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to see booking details");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public List<BookingResponseDto> getBookingsByUser(Long userId, State state, int page, int size) {
        getUserOrThrow(userId);
        PageRequest pageR = PageRequest.of(page, size);
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookings(userId);
            case PAST -> bookingRepository.findPastBookings(userId);
            case FUTURE -> bookingRepository.findFutureBookings(userId);
            case WAITING -> bookingRepository.findBookingsByStatus(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findBookingsByStatus(userId, Status.REJECTED);
            default -> bookingRepository.findBookingsByBookerId(userId, pageR).getContent();

        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, State state) {
        getUserOrThrow(ownerId);
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookingsByOwner(ownerId);
            case PAST -> bookingRepository.findPastBookingsByOwner(ownerId);
            case FUTURE -> bookingRepository.findFutureBookingsByOwner(ownerId);
            case WAITING -> bookingRepository.findBookingsByOwnerAndStatus(ownerId, Status.WAITING);
            case REJECTED -> bookingRepository.findBookingsByOwnerAndStatus(ownerId, Status.REJECTED);
            default -> bookingRepository.findBookingsByOwner(ownerId);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Booking getBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found with id " + id));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id " + id));
    }
}
