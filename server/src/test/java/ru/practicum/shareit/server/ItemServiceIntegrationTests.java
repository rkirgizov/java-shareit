package ru.practicum.shareit.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.dto.comment.CommentDto;
import ru.practicum.shareit.server.item.dto.item.*;
import ru.practicum.shareit.server.item.service.ItemService;
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
public class ItemServiceIntegrationTests {

    private final EntityManager em;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    private User user;
    private User owner;

    private static ItemCreateDto itemCreateDto;
    private static ItemUpdateDto itemUpdateDto;
    private static CommentDto commentDto;
    private static UserCreateDto userCreateDto1;
    private static UserCreateDto userCreateDto2;
    private static BookingDto bookingDto;
    private static BookingCreateDto bookingCreateDto;
    private static LocalDateTime now;

    @BeforeAll
    static void dtoSetup() {
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
        itemUpdateDto = ItemUpdateDto.builder()
                .name("Improved Drill")
                .description("Even more powerful")
                .build();
        commentDto = CommentDto.builder()
                .text("Excellent!")
                .build();
        bookingCreateDto = BookingCreateDto.builder()
                .start(now.minusDays(1))
                .end(now.minusHours(1))
                .build();
    }

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserMapper.toUser(userCreateDto1));
        owner = userRepository.save(UserMapper.toUser(userCreateDto2));
    }

    @Test
    void testCreateItem_success() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void testCreateItem_requestNotFound() {
        itemCreateDto.setRequestId(1L);

        assertThrows(NotFoundException.class, () ->
                itemService.createItem(owner.getId(), itemCreateDto));
    }

    @Test
    void testCreateItem_userNotFound_throwsNotFoundException() {

        assertThrows(NotFoundException.class, () ->
                itemService.createItem(999L, itemCreateDto));
    }

    @Test
    void testUpdateItem_success() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        ItemDto result = itemService.updateItem(itemDto.getOwner(), itemDto.getId(), itemUpdateDto);

        assertNotNull(result);
        assertEquals("Improved Drill", result.getName());
        assertEquals("Even more powerful", result.getDescription());
    }

    @Test
    void testUpdateItem_notOwner_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(999L, 999L, itemUpdateDto));
    }

    @Test
    void testGetItemById_success() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        ItemViewingDto result = itemService.getItemById(itemDto.getOwner(), itemDto.getId());

        assertNotNull(result);
        assertEquals("Saw", result.getName());
    }

    @Test
    void testGetItemById_notFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                itemService.getItemById(999L, 999L));
    }

    @Test
    void testFindAllItems_success() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        List<ItemOwnerViewingDto> result = itemService.findAllItems(itemDto.getOwner()).stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

    }

    @Test
    void testFindAllItems_success_branch1() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        List<ItemOwnerViewingDto> result = itemService.findAllItems(user.getId()).stream().toList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

    }

    @Test
    void testSearchItems_success() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        Collection<ItemDto> result = itemService.searchItems("Saw");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testSearchItems_emptyQuery_returnsEmpty() {
        Collection<ItemDto> result = itemService.searchItems("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateComment_success() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        bookingCreateDto.setItemId(itemDto.getId());
        BookingDto bookingDto = bookingService.createBooking(user.getId(), bookingCreateDto);
        CommentDto result = itemService.createComment(user.getId(), itemDto.getId(), commentDto);
        assertNotNull(result);
        assertEquals("Excellent!", result.getText());

    }

    @Test
    void testCreateComment_userDidNotBook_throwsValidationException() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        bookingCreateDto.setItemId(itemDto.getId());
        BookingDto bookingDto = bookingService.createBooking(user.getId(), bookingCreateDto);
        assertThrows(ValidationException.class, () ->
                itemService.createComment(owner.getId(), itemDto.getId(), commentDto));
    }

    @Test
    void testFindAllItems_success_branch2() {
        ItemDto itemDto = itemService.createItem(owner.getId(), itemCreateDto);
        bookingCreateDto.setItemId(itemDto.getId());
        BookingDto bookingDto = bookingService.createBooking(user.getId(), bookingCreateDto);
        CommentDto commnet = itemService.createComment(user.getId(), itemDto.getId(), commentDto);
        List<ItemOwnerViewingDto> result = itemService.findAllItems(itemDto.getOwner()).stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());


    }

}