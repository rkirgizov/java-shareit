package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.dto.item.ItemCreateDto;
import ru.practicum.shareit.item.dto.item.ItemResponseDto;
import ru.practicum.shareit.item.dto.item.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestBody @Valid ItemCreateDto itemCreateDto) {
        ItemResponseDto item = itemService.create(userId, itemCreateDto);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createCommentForItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        CommentResponseDto commentDto = commentService.createComment(userId, itemId, commentCreateDto);
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long itemId,
                                                      @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        ItemResponseDto item = itemService.update(userId, itemId, itemUpdateDto);
        return new ResponseEntity<>(item, HttpStatus.OK);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long itemId) {
        ItemResponseDto item = itemService.getItemById(itemId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemResponseDto> items = itemService.getItemsByOwnerId(userId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam String text) {
        List<ItemResponseDto> items = itemService.searchItems(text);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{itemId}/comment")
    public ResponseEntity<List<CommentResponseDto>> getCommentsForItem(
            @PathVariable Long itemId) {
        List<CommentResponseDto> commentDtos = commentService.getCommentsForItem(itemId);
        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }
}