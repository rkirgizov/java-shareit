package ru.practicum.shareit.server.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
}
