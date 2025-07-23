package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.server.item.dto.comment.CommentDto;
import ru.practicum.shareit.server.item.dto.item.*;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, ItemCreateDto itemCreateDto);

    ItemDto updateItem(long userId, long itemId, ItemUpdateDto itemUpdateDto);

    ItemViewingDto getItemById(long userId, long itemId);

    Collection<ItemOwnerViewingDto> findAllItems(long userId);

    Collection<ItemDto> searchItems(String searchQuery);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);

}