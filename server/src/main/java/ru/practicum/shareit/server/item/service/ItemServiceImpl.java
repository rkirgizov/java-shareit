package ru.practicum.shareit.server.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.repo.BookingRepository;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.Comment;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.dto.item.*;
import ru.practicum.shareit.server.item.dto.comment.CommentDto;
import ru.practicum.shareit.server.item.repo.CommentRepository;
import ru.practicum.shareit.server.item.repo.ItemRepository;
import ru.practicum.shareit.server.request.repo.ItemRequestRepository;
import ru.practicum.shareit.server.request.Request;
import ru.practicum.shareit.server.user.repo.UserRepository;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static ru.practicum.shareit.server.common.ServerCheckUtility.*;
import static ru.practicum.shareit.server.item.dto.comment.CommentMapper.*;
import static ru.practicum.shareit.server.item.dto.item.ItemMapper.*;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(long userId, ItemCreateDto itemCreateDto) {
        getUser(userId);
        itemCreateDto.setOwner(userId);

        Request request = null;
        if (Objects.nonNull(itemCreateDto.getRequestId())) {
            request = getRequest(itemCreateDto.getRequestId());
        }

        return toItemDto(
                itemRepository.save(toItem(itemCreateDto, request)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        getUser(userId);
        Item oldItem = getItem(itemId);
        isOwner(userId, oldItem.getOwner());
        return toItemDto(
                itemRepository.save(toItem(oldItem, itemUpdateDto)));
    }

    @Override
    public ItemViewingDto getItemById(long userId, long itemId) {
        getUser(userId);
        Item item = getItem(itemId);
        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;
        LocalDateTime requestTime = LocalDateTime.now();
        if (isOwnerBoolean(userId, item.getOwner())) {
            List<Booking> bookingListBefore =
                    bookingRepository.findByItem_IdAndEndIsBeforeOrderByEndAsc(itemId, requestTime);
            List<Booking> bookingListAfter =
                    bookingRepository.findByItem_IdAndStartIsAfterOrderByEndAsc(itemId, requestTime);
            if (!bookingListBefore.isEmpty()) lastBooking = bookingListBefore.getLast().getEnd();
            if (!bookingListAfter.isEmpty()) nextBooking = bookingListAfter.getFirst().getEnd();
        }
        List<Long> commentsList = commentRepository.findByItem_IdOrderByCreatedAsc(itemId)
                .stream()
                .map(Comment::getId)
                .toList();
        return toItemViewingDto(item, lastBooking, nextBooking, commentsList);
    }

    @Override
    public Collection<ItemOwnerViewingDto> findAllItems(long userId) {
        getUser(userId);
        List<Item> itemList = itemRepository.findByOwner(userId);
        List<Long> itemIdList = itemList.stream()
                .map(Item::getId)
                .toList();
        List<Booking> bookingList = bookingRepository.findByItem_IdInOrderByStartAsc(itemIdList);
        Map<Long, List<Booking>> ownerBookingMap = bookingList.stream()
                .collect(groupingBy(booking -> booking.getItem().getId()));

        List<Comment> commentList = commentRepository.findByItem_IdInOrderByCreatedAsc(itemIdList);
        Map<Long, List<Comment>> ownerCommentsMap = commentList.stream()
                .collect(groupingBy(comment -> comment.getItem().getId()));


        return itemList.stream()
                .map(item -> {
                    LocalDateTime lastBooking = null;
                    LocalDateTime nextBooking = null;
                    List<Long> comments = new ArrayList<>();
                    LocalDateTime requestTime = LocalDateTime.now();
                    if (Objects.nonNull(ownerBookingMap.get(item.getId()))) {
                        if (!ownerBookingMap.get(item.getId()).isEmpty()) {
                            List<LocalDateTime> lastBookingDateList = ownerBookingMap.get(item.getId()).stream()
                                    .map(Booking::getEnd)
                                    .filter(end -> end.isBefore(requestTime))
                                    .toList();
                            if (!lastBookingDateList.isEmpty())
                                lastBooking = lastBookingDateList.getLast();
                        }

                        if (!ownerBookingMap.get(item.getId()).isEmpty()) {
                            List<LocalDateTime> nextBookingDateList = ownerBookingMap.get(item.getId()).stream()
                                    .map(Booking::getEnd)
                                    .filter(end -> end.isAfter(requestTime))
                                    .toList();
                            if (!nextBookingDateList.isEmpty())
                                nextBooking = nextBookingDateList.getFirst();
                        }

                    }
                    if (Objects.nonNull(ownerCommentsMap.get(item.getId()))) {
                        if (!ownerCommentsMap.get(item.getId()).isEmpty())
                            comments.addAll(ownerCommentsMap.get(item.getId()).stream()
                                    .map(Comment::getId)
                                    .toList());
                    }

                    return ItemMapper.toItemOwnerRequestDtoV2(item, lastBooking, nextBooking, comments);
                })
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String searchQuery) {
        return itemRepository.search(searchQuery).stream()
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User booker = getUser(userId);
        Item item = getItem(itemId);
        LocalDateTime requestTime = LocalDateTime.now();
        bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId, requestTime)
                .orElseThrow(() ->
                        new ValidationException(String.format("Пользователь с ID %s не пользовался вещью с ID %s до %s",
                                userId, itemId, requestTime)));
        return toCommentDto(commentRepository.save(toComment(commentDto, booker, item)));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
    }

    private Request getRequest(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с ID %s не найден", requestId)));
    }

}
