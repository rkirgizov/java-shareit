package ru.practicum.shareit.server.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestAnswerDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<AnswerDto> items;
}
