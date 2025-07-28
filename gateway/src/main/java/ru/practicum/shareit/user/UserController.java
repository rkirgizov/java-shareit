package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController { // Контроллер в модуле gateway

    private final UserClient userClient; // Инжектим клиента вместо сервиса

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) { // Валидация на уровне контроллера
        // Делегируем вызов клиенту
        // userId не передается в заголовке для создания пользователя
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        // Делегируем вызов клиенту
        // userId передается как путь, может также передаваться в заголовке, если сервер ожидает
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        // Делегируем вызов клиенту
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        // Делегируем вызов клиенту
        // userId передается как путь, userId в заголовке не требуется (судя по оригинальному контроллеру)
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        // Делегируем вызов клиенту
        return userClient.getAllUsers();
    }
}
