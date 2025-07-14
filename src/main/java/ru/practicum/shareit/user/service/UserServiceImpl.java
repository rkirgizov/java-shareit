package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = findUserByIdOrThrow(userId);
        return UserMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User existringUser = findUserByIdOrThrow(userId);
        repository.delete(existringUser);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto userDto) {
        if (repository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("User with email " + userDto.getEmail() + " already exists");
        }
        User savedUser = repository.save(UserMapper.toCreateUser(userDto));
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto userDto) {
        User existingUser = findUserByIdOrThrow(userId);

        if (repository.findByEmail(userDto.getEmail()).isPresent() &&
                !userDto.getEmail().equals(existingUser.getEmail()))
            throw new ConflictException("User with email " + userDto.getEmail() + " already exists");

        if (userDto.getName() != null) existingUser.setName(userDto.getName());
        if (userDto.getEmail() != null) existingUser.setEmail(userDto.getEmail());
        User updatedUser = repository.save(existingUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    private User findUserByIdOrThrow(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + userId));
    }
}