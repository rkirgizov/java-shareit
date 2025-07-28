package ru.practicum.shareit.request;

import jakarta.validation.Valid; // Импорт для @Valid, если будет валидация DTO запроса
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController { // Контроллер в модуле gateway

    private final ItemRequestClient itemRequestClient; // Инжектим клиента вместо сервиса

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestDto dto) { // Валидация на уровне контроллера
        // Делегируем вызов клиенту
        return itemRequestClient.createItemRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        // Делегируем вызов клиенту
        return itemRequestClient.getAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        // Делегируем вызов клиенту
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        // Делегируем вызов клиенту
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
