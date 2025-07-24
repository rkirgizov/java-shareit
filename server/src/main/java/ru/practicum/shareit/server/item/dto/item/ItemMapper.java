package ru.practicum.shareit.server.item.dto.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.request.dto.AnswerDto;
import ru.practicum.shareit.server.request.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(Objects.nonNull(item.getRequest()) ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemCreateDto itemCreateDto, Request request) {
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setOwner(itemCreateDto.getOwner());
        if (Objects.nonNull(request)) item.setRequest(request);

        return item;
    }

    public static Item toItem(Item oldItem, ItemUpdateDto itemUpdateDto) {
        if (Objects.nonNull(itemUpdateDto.getName())) oldItem.setName(itemUpdateDto.getName());
        if (Objects.nonNull(itemUpdateDto.getDescription())) oldItem.setDescription(itemUpdateDto.getDescription());
        if (Objects.nonNull(itemUpdateDto.getAvailable())) oldItem.setAvailable(itemUpdateDto.getAvailable());
        return oldItem;
    }

    public static ItemOwnerViewingDto toItemOwnerRequestDto(Item item) {
        return ItemOwnerViewingDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

    public static ItemOwnerViewingDto toItemOwnerRequestDtoV2(
            Item item, LocalDateTime lastBooking, LocalDateTime nextBooking, List<Long> commentsId) {
        return ItemOwnerViewingDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentsId)
                .build();
    }

    public static ItemViewingDto toItemViewingDto(
            Item item, LocalDateTime lastBooking, LocalDateTime nextBooking, List<Long> commentsList) {
        return ItemViewingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentsList)
                .build();
    }

    public static AnswerDto toItemAnswer(Item item) {
        return AnswerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(item.getOwner())
                .build();
    }

}