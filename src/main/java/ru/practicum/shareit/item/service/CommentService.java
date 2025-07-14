package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(Long userId, Long itemId, CommentCreateDto dto);

    List<CommentResponseDto> getCommentsForItem(Long itemId);
}