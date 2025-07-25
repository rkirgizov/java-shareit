package ru.practicum.shareit.server.item.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;
    private String text;
    private long item;
    private String authorName;
    private LocalDateTime created;
}
