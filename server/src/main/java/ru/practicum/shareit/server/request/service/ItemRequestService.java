package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.server.request.dto.RequestAnswerDto;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.dto.RequestCreateDto;

import java.util.List;

public interface ItemRequestService {
    RequestDto createItemRequest(long userId, RequestCreateDto requestCreateDto);

    List<RequestAnswerDto> findAllByOwnerRequests(long userId);

    List<RequestDto> findAllRequests(long userId);

    RequestAnswerDto getRequestById(long userId, long requestId);
}
