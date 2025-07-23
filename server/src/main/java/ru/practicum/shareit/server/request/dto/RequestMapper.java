package ru.practicum.shareit.server.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.request.Request;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static Request toItemRequest(long requestor, RequestCreateDto requestCreateDto) {
        Request request = new Request();
        request.setDescription(requestCreateDto.getDescription());
        request.setRequestor(requestor);
        return request;
    }

    public static RequestDto toItemRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static RequestAnswerDto toItemRequestAnswerDto(Request request) {
        return RequestAnswerDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(request.getItems())
                .build();
    }

}
