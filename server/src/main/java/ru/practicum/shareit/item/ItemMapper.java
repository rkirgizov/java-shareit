package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;

public class ItemMapper {
    public static Item toItem(ItemDto dto, Long ownerId) {
        User owner = new User();
        owner.setId(ownerId);
        return new Item(dto.getId(), dto.getName(), dto.getDescription(), dto.getAvailable(), owner, null);
    }

    public static ItemDto toItemDto(Item item,
                                    BookingShortDto lastBooking,
                                    BookingShortDto nextBooking,
                                    List<CommentDto> comments) {
        Long ownerId = (item.getOwner() != null) ? item.getOwner().getId() : null;
        Long requestId = item.getRequestId();
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                ownerId,
                lastBooking,
                nextBooking,
                comments,
                requestId
        );
    }

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        Long ownerId = (item.getOwner() != null) ? item.getOwner().getId() : null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                ownerId,
                null,
                null,
                Collections.emptyList(),
                item.getRequestId()
        );
    }
}