package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.item.ItemCreateDto;
import ru.practicum.shareit.item.dto.item.ItemResponseDto;
import ru.practicum.shareit.item.dto.item.ItemUpdateDto;

import java.util.List;


public interface ItemService {

    ItemResponseDto create(Long userId, ItemCreateDto itemCreateDto);

    ItemResponseDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    ItemResponseDto getItemById(Long id);

    List<ItemResponseDto> getItemsByOwnerId(Long id);

    List<ItemResponseDto> searchItems(String text);
}