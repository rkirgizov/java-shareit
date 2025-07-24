package ru.practicum.shareit.server.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.repo.UserRepository;

import java.util.Collection;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        return UserMapper.toUserDto(
                userRepository.save(UserMapper.toUser(userCreateDto)));
    }

    @Override
    public Collection<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto findUserById(long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", id))));
    }

    @Override
    public UserDto updateUser(long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", id)));
        return UserMapper.toUserDto(
                userRepository.save(UserMapper.toUser(user, userUpdateDto)));
    }

    @Override
    public UserDto deleteUser(long id) {
        User userToDelete = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", id)));
        userRepository.delete(userToDelete);
        return UserMapper.toUserDto(userToDelete);
    }

}
