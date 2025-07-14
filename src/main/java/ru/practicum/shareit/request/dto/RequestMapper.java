package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.Request;

@Component
public class RequestMapper {
    public static RequestDto toItemRequestDto(Request itemRequest) {
        return new RequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated()
        );
    }

    public static Request toItemRequest(RequestDto itemRequestDto) {
        return new Request(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequester(),
                itemRequestDto.getCreated()
        );
    }
}