package ru.practicum.shareit.server.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.comment.CommentDto;
import ru.practicum.shareit.server.item.dto.item.*;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.createItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable("itemId") long itemId,
                          @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ItemViewingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable("itemId") long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemOwnerViewingDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text", required = false) String searchQuery) {
        return itemService.searchItems(searchQuery);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable("itemId") long itemId,
                             @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId,itemId, commentDto);
    }

}