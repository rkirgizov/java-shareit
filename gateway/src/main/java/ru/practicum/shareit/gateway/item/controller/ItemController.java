package ru.practicum.shareit.gateway.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.ItemClient;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;


@Controller
@RequestMapping(path = "/items")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Запрос пользователя {} на создание вещи {}",userId, itemCreateDto);
        return itemClient.createItem(userId, itemCreateDto);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Positive @PathVariable("itemId") long itemId,
                                         @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Запрос {} пользователя {} на обновление вещи {}", itemUpdateDto, userId,itemId);
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                          @Positive @PathVariable("itemId") long itemId) {
        log.info("Запрос на получение вещи {} от пользователя {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(
            @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение всех вещей пользователя {}", userId);
        return itemClient.findAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text", required = false) String searchQuery) {
        log.info("Запрос на поиск - {}",searchQuery);
        return itemClient.searchItems(searchQuery);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Positive @PathVariable("itemId") long itemId,
                                         @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос {} пользователя {} на создание комментария о вещи {}",commentDto,  userId, itemId);
        return itemClient.sreateComments(userId, itemId, commentDto);
    }

}