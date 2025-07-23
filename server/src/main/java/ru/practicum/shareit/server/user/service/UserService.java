package ru.practicum.shareit.server.user.service;

import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserCreateDto userCreateDto);

    Collection<UserDto> findAllUsers();

    UserDto findUserById(long id);

    UserDto updateUser(long id, UserUpdateDto userDto);

    UserDto deleteUser(long id);
}
