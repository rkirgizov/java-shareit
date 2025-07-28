package ru.practicum.shareit.comment;

public interface CommentService {
    CommentDto addComment(Long itemId, Long userId, String text);
}