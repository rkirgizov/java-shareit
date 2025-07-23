package ru.practicum.shareit.server.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {
    private String name;
    private String email;
}
