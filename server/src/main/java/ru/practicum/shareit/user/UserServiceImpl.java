package ru.practicum.shareit.user;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Email is already in use");
        }

        User user = UserMapper.toUser(userDto);
        user.setId(null);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            userRepository.findByEmail(userDto.getEmail())
                    .filter(u -> !u.getId().equals(userId))
                    .ifPresent(u -> { throw new ConflictException("Email is already in use"); });
            existing.setEmail(userDto.getEmail());
        }

        existing = userRepository.save(existing);
        return UserMapper.toUserDto(existing);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }
}
