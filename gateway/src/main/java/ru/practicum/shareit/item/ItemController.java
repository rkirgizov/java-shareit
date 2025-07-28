package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController { // Контроллер в модуле gateway

    private final ItemClient itemClient; // Инжектим клиента вместо сервиса

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto) { // Валидация на уровне контроллера
        // Делегируем вызов клиенту
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) { // Валидация на уровне DTO (если есть)
        // Делегируем вызов клиенту
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId) {
        // Делегируем вызов клиенту
        // itemId передается как путь, userId не требуется в заголовке для этого эндпоинта (судя по оригинальному контроллеру)
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        // Делегируем вызов клиенту
        return itemClient.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        // Делегируем вызов клиенту
        // text передается как параметр запроса, userId не требуется в заголовке (судя по оригинальному контроллеру)
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) { // Валидация на уровне контроллера
        // Делегируем вызов клиенту
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
