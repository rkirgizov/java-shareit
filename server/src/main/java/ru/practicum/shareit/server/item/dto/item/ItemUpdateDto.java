package ru.practicum.shareit.server.item.dto.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemUpdateDto {
    private String name;
    private String description;
    private Boolean available;
}
