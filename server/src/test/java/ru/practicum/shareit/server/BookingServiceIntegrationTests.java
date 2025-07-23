package ru.practicum.shareit.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.repo.BookingRepository;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.booking.enumeration.Status;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.repo.ItemRepository;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.item.dto.item.ItemDto;
import ru.practicum.shareit.server.item.dto.item.ItemCreateDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.request.dto.RequestCreateDto;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.repo.UserRepository;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
//@Rollback(false)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTests {

    private final EntityManager em;

    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;
    private static User user;
    private static User owner;
    private static RequestCreateDto requestCreateDto;
    private static RequestCreateDto otherRequest;
    private static UserCreateDto userCreateDto1;
    private static UserCreateDto userCreateDto2;
    private static ItemCreateDto itemCreateDto;
    private static LocalDateTime now;
    private static BookingCreateDto bookingCreateDto;

    @BeforeAll
    static void modelSetup() {
        now = LocalDateTime.now();
        userCreateDto1 = UserCreateDto.builder()
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();

        userCreateDto2 = UserCreateDto.builder()
                .name("Billie Ryan")
                .email("Citlalli59@hotmail.com")
                .build();

        itemCreateDto = ItemCreateDto.builder()
                .name("Saw")
                .description("Hand saw")
                .available(true)
                .owner(1)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .start(now.minusDays(1))
                .end(now.minusHours(1))
                .build();
        requestCreateDto = RequestCreateDto.builder()
                .description("Need a drill")
                .build();
    }

    @BeforeEach
    void setup() {
        user = userRepository.save(UserMapper.toUser(userCreateDto1));
        owner = userRepository.save(UserMapper.toUser(userCreateDto2));
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        bookingCreateDto.setItemId(itemDto.getId());
    }

    @Test
    void testCreateBooking_success() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
//        bookingCreateDto.setItemId(itemDto.getId());
        BookingDto result = bookingService.createBooking(user.getId(), bookingCreateDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
        assertNotNull(result.getId());
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.status = :status",
                        Booking.class);
        Booking booking = query.setParameter("status", result.getStatus())
                .getSingleResult();
        assertThat(result.getId(), notNullValue());
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getStatus(), equalTo(booking.getStatus()));
    }


    @Test
    void testCreateBooking_userNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(999L, bookingCreateDto));
    }

    @Test
    void testCreateBooking_itemNotFound_throwsNotFoundException() {
        bookingCreateDto.setItemId(999L);
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(user.getId(), bookingCreateDto));
    }

    @Test
    void testUpdateBooking_approved_success() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
//        bookingCreateDto.setItemId(itemDto.getId());
        BookingDto booking = bookingService.createBooking(user.getId(), bookingCreateDto);
        BookingDto updated = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertNotNull(updated);
        assertEquals(Status.APPROVED, updated.getStatus());
    }

    @Test
    void testUpdateBooking_rejected_success() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
//        bookingCreateDto.setItemId(itemDto.getId());
        BookingDto booking = bookingService.createBooking(user.getId(), bookingCreateDto);
        BookingDto updated = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        assertNotNull(updated);
        assertEquals(Status.REJECTED, updated.getStatus());
    }

    @Test
    void testUpdateBooking_notOwner_throwsNotFoundException() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
//        bookingCreateDto.setItemId(itemDto.getId());
        BookingDto booking = bookingService.createBooking(user.getId(), bookingCreateDto);
        assertThrows(ValidationException.class, () ->
                bookingService.updateBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void testGetBookingById_success() {
        BookingDto booking = bookingService.createBooking(user.getId(), bookingCreateDto);
        BookingDto result = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void testGetBookingById_notFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(user.getId(), 999L));
    }

    @Test
    void testFindAllBookings_stateAll_success() {
        bookingService.createBooking(user.getId(), bookingCreateDto);
        Collection<BookingStateRequestDto> result = bookingService.findAllBookings(user.getId(), "ALL");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindAllBookings_stateWaiting_success() {
        bookingService.createBooking(user.getId(), bookingCreateDto);
        List<BookingStateRequestDto> resultBooker =
                bookingService.findAllBookings(user.getId(), "WAITING").stream().toList();

        assertNotNull(resultBooker);
        assertFalse(resultBooker.isEmpty());
        assertEquals(1, resultBooker.size());
        assertEquals("WAITING", resultBooker.getFirst().getStatus().name());
    }

    @Test
    void testFindAllOwnerBookings_stateAll_success() {
        bookingService.createBooking(user.getId(), bookingCreateDto);
        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "ALL").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindAllBookings_stateRejected_success() {
        BookingDto booking = bookingService.createBooking(user.getId(), bookingCreateDto);
        BookingDto updated = bookingService.updateBooking(owner.getId(), booking.getId(), false);
//        bookingService.createBooking(user.getId(), bookingCreateDto);
        List<BookingStateRequestDto> resultBooker =
                bookingService.findAllBookings(user.getId(), "REJECTED").stream().toList();

        assertNotNull(resultBooker);
        assertFalse(resultBooker.isEmpty());
        assertEquals(1, resultBooker.size());
        assertEquals("REJECTED", resultBooker.getFirst().getStatus().name());
    }

    @Test
    void testFindAllOwnerBookings_stateWaiting_success() {
        BookingDto bookingCurrent = bookingService.createBooking(user.getId(), bookingCreateDto);
//        bookingService.updateBooking(owner.getId(), bookingCurrent.getId(), true);

        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "WAITING").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("WAITING", result.getFirst().getStatus().name());
    }

    @Test
    void testFindAllOwnerBookings_stateRejected_success() {
        BookingDto bookingCurrent = bookingService.createBooking(user.getId(), bookingCreateDto);
        bookingService.updateBooking(owner.getId(), bookingCurrent.getId(), false);

        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "REJECTED").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("REJECTED", result.getFirst().getStatus().name());
    }

    @Test
    void testFindAllOwnerBookings_state_success() {
        // findAllOwnerBooking
        bookingCreateDto.setStart(now.minusDays(1));
        bookingCreateDto.setEnd(now.plusDays(1));
        BookingDto bookingCurrent = bookingService.createBooking(user.getId(), bookingCreateDto);
        bookingService.updateBooking(owner.getId(), bookingCurrent.getId(), true);

        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "CURRENT").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("CURRENT", result.getFirst().getStatus().name());

        // findAllBookings
        List<BookingStateRequestDto> resultBookerCurrent =
                bookingService.findAllBookings(user.getId(), "CURRENT").stream().toList();

        assertNotNull(resultBookerCurrent);
        assertFalse(resultBookerCurrent.isEmpty());
        assertEquals(1, resultBookerCurrent.size());
        assertEquals("CURRENT", resultBookerCurrent.getFirst().getStatus().name());

        // findAllOwnerBooking
        bookingCreateDto.setStart(now.minusDays(1));
        bookingCreateDto.setEnd(now.minusHours(1));
        BookingDto bookingPast = bookingService.createBooking(user.getId(), bookingCreateDto);
        bookingService.updateBooking(owner.getId(), bookingPast.getId(), true);

        List<BookingStateRequestDto> resultPast =
                bookingService.findAllOwnerBooking(owner.getId(), "PAST").stream().toList();

        assertNotNull(resultPast);
        assertFalse(resultPast.isEmpty());
        assertEquals("PAST", resultPast.getFirst().getStatus().name());

        // findAllBookings
        List<BookingStateRequestDto> resultBookerPast =
                bookingService.findAllBookings(user.getId(), "PAST").stream().toList();

        assertNotNull(resultBookerPast);
        assertFalse(resultBookerPast.isEmpty());
        assertEquals(1, resultBookerPast.size());
        assertEquals("PAST", resultBookerPast.getFirst().getStatus().name());

        // findAllOwnerBooking
        bookingCreateDto.setStart(now.plusDays(1));
        bookingCreateDto.setEnd(now.plusDays(2));
        BookingDto bookingFuture = bookingService.createBooking(user.getId(), bookingCreateDto);
        bookingService.updateBooking(owner.getId(), bookingFuture.getId(), true);

        List<BookingStateRequestDto> resultFuture =
                bookingService.findAllOwnerBooking(owner.getId(), "FUTURE").stream().toList();

        assertNotNull(resultFuture);
        assertFalse(resultFuture.isEmpty());
        assertEquals("FUTURE", resultFuture.getFirst().getStatus().name());

        // findAllBookings
        List<BookingStateRequestDto> resultBookerFuture =
                bookingService.findAllBookings(user.getId(), "FUTURE").stream().toList();

        assertNotNull(resultBookerFuture);
        assertFalse(resultBookerFuture.isEmpty());
        assertEquals(1, resultBookerFuture.size());
        assertEquals("FUTURE", resultBookerFuture.getFirst().getStatus().name());

    }


}
