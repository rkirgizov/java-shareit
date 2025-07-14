package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long itemId, CommentCreateDto dto) {
        Item item = getItemOrThrow(itemId);
        User author = getUserOrThrow(userId);
        List<Booking> bookings = bookingRepository.findCompletedBookings(itemId, userId);
        if (bookings.isEmpty()) {
            throw new BadRequestException("User cannot comment, has not rented this item");
        }
        Comment comment = CommentMapper.toComment(dto, item, author);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public List<CommentResponseDto> getCommentsForItem(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
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