package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addComment(Long itemId, Long userId, String text) {
        List<?> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new NotFoundException("Пользователь не арендировал эту вещь");
        }

        Item item = new Item();
        item.setId(itemId);

        User user = new User();
        user.setId(userId);

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return commentMapper.toCommentDto(saved);
    }
}
