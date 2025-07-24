package ru.practicum.shareit.gateway.request.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.request.Request;
import ru.practicum.shareit.gateway.request.dto.ItemRequestCreateDto;


@Controller
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final Request request;

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Запрос пользователя {} на создание запроса {}", userId, itemRequestCreateDto);
        return request.createRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на список запросов текущего пользователя {}", userId);
        return request.fiindAllRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос пользователя {} на список запросов, созданных другими пользователями",userId);
        return request.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                          @Positive @PathVariable("requestId") long requestId) {
        log.info("Запрос на получение данных об запросе {} вместе с данными об ответах", requestId);
        return request.getRequestById(userId, requestId);
    }
}
