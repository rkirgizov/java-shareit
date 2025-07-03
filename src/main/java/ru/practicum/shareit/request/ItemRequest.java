package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequest {
    private Long id;
    private Long requester;
    private String description;
    private LocalDate created;
}
