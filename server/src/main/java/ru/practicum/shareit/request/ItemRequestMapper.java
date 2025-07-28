package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto dto, User requester) {
        return new ItemRequest(dto.getId(), dto.getDescription(), requester, LocalDateTime.now());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(Collections.emptyList())
                .build();
    }
}
