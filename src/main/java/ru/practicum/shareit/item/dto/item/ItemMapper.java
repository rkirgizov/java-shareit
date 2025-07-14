package ru.practicum.shareit.item.dto.item;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemResponseDto toResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner().getId(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static ItemResponseDto toResponseDto(Item item,
                                                List<BookingResponseDto> bookings,
                                                List<CommentResponseDto> comments) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner().getId(),
                item.getAvailable(),
                null,
                null,
                bookings,
                comments
        );
    }

    public static Item toItem(ItemCreateDto itemCreateDto, User owner) {
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static Item toItem(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        return item;
    }
}