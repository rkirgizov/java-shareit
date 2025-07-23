package ru.practicum.shareit.server.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") long id,
                          @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable("id") long id) {
        return userService.deleteUser(id);
    }
}
