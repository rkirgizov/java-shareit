package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestDto {

    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}