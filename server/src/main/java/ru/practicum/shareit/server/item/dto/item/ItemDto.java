package ru.practicum.shareit.server.item.dto.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private Long requestId;

}