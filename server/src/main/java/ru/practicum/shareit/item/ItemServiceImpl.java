package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

        LocalDateTime now = LocalDateTime.now();

        BookingShortDto lastBooking = null;

        BookingShortDto nextBooking = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                .stream()
                .findFirst()
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        Item item = ItemMapper.toItem(itemDto, userId);
        item.setId(null);
        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User with id=" + userId + " is not the owner of item with id=" + itemId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updated = itemRepository.save(item);
        return ItemMapper.toItemDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        return items.stream()
                .map(item -> {
                    LocalDateTime now = LocalDateTime.now();
                    BookingShortDto lastBooking = bookingRepository.findByItemIdAndEndBeforeOrderByStartDesc(item.getId(), now)
                            .stream()
                            .findFirst()
                            .map(BookingMapper::toBookingShortDto)
                            .orElse(null);
                    BookingShortDto nextBooking = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(item.getId(), now)
                            .stream()
                            .findFirst()
                            .map(BookingMapper::toBookingShortDto)
                            .orElse(null);
                    List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId())
                            .stream()
                            .map(commentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.searchByText(text.toLowerCase());
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException("User has not completed a booking for this item and cannot comment");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return commentMapper.toCommentDto(saved);
    }
}