package ru.practicum.shareit.server.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.server.booking.enumeration.State;
import ru.practicum.shareit.server.item.dto.item.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingStateRequestDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    @Builder.Default
    private State status = State.WAITING;
}
