package ru.practicum.shareit.server.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.request.dto.RequestAnswerDto;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.dto.RequestCreateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public RequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody RequestCreateDto requestCreateDto) {
        return itemRequestService.createItemRequest(userId, requestCreateDto);
    }

    @GetMapping
    public List<RequestAnswerDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findAllByOwnerRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestAnswerDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable("requestId") long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
