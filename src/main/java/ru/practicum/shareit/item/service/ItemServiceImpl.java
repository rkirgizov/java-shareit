package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.dto.item.ItemCreateDto;
import ru.practicum.shareit.item.dto.item.ItemResponseDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.dto.item.ItemUpdateDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemResponseDto create(Long userId, ItemCreateDto itemCreateDto) {
        User owner = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(itemCreateDto, owner);
        Item createdItem = itemRepository.save(item);
        return ItemMapper.toResponseDto(createdItem);
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        Item existingItem = getItemOrThrow(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can update the item");
        }
        Item updatedItem = ItemMapper.toItem(existingItem, itemUpdateDto);
        itemRepository.save(updatedItem);
        return ItemMapper.toResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto getItemById(Long id) {
        Item item = getItemOrThrow(id);
        List<Comment> comments = commentRepository.findAllByItemId(id);
        List<CommentResponseDto> commentDtos = comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemResponseDto dto = ItemMapper.toResponseDto(item);
        dto.setComments(commentDtos);
        return dto;
    }

    @Override
    public List<ItemResponseDto> getItemsByOwnerId(Long id) {
        getUserOrThrow(id);
        List<Item> items = itemRepository.findByOwnerId(id);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findApprovedByItemIdsOrderByStartAsc(itemIds);

        Map<Long, List<BookingResponseDto>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(),
                        Collectors.mapping(BookingMapper::toBookingDto, Collectors.toList())
                ));

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);

        Map<Long, List<CommentResponseDto>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));
        return items
                .stream()
                .map(item -> {
                    List<BookingResponseDto> bookingsDto = bookingsByItemId
                            .getOrDefault(item.getId(), Collections.emptyList());
                    List<CommentResponseDto> commentsDto = commentsByItemId
                            .getOrDefault(item.getId(), Collections.emptyList());
                    return ItemMapper.toResponseDto(item, bookingsDto, commentsDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerCaseText = text.toLowerCase();
        List<Item> items = itemRepository.searchItems(lowerCaseText);
        return items
                .stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id " + id));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Owner not found with id " + id));
    }
}