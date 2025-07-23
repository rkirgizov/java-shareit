package ru.practicum.shareit.gateway.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.UserClient;
import ru.practicum.shareit.gateway.user.dto.UserCreateDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Запрос на получение списка пользователей");
        return userClient.getUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@Positive @PathVariable("id") long id) {
        log.info("Запрос на получения пользователя по ID {}", id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Запрос на создание  пользователя {}", userCreateDto);
        return userClient.createUser(userCreateDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Positive @PathVariable("id") long id,
                                         @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Запрос на обновление пользователя {}", id);
        return userClient.updateUser(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@Positive @PathVariable("id") long id) {
        log.info("Запрос на удаление пользователя {}", id);
        return userClient.deleteUser(id);
    }

}