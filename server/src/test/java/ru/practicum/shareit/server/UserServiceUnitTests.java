package ru.practicum.shareit.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.repo.UserRepository;
import ru.practicum.shareit.server.user.service.UserServiceImpl;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    @InjectMocks
    private UserServiceImpl mockUserServiceImpl;

    @Mock
    private UserRepository userRepository;

    private User user;
    private UserCreateDto userCreateDto;
    private List<User> userList;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setup() {
        userCreateDto = UserCreateDto.builder()
                .id(1)
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();
        userUpdateDto = UserUpdateDto.builder()
                .email("Genesis22@gmail.com")
                .name("Ms. Cesar Funk")
                .build();
        user = new User();
        user.setId(1);
        user.setName("Ms. Cesar Funk");
        user.setEmail("Genesis22@gmail.com");
        userList = List.of(user);
        mockUserServiceImpl = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreateUser() {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        UserDto mockUserDto = mockUserServiceImpl.createUser(userCreateDto);
        assertEquals("Ms. Cesar Funk", mockUserDto.getName());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(any());
    }

    @Test
    void testFindAllUsers() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(userList);
        assertEquals(1, mockUserServiceImpl.findAllUsers().size());
        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void testfindUserById() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        assertEquals("Genesis22@gmail.com", mockUserServiceImpl.findUserById(1L).getEmail());
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserServiceImpl.findUserById(2));
        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(2))
                .findById(anyLong());
    }

    @Test
    void testUpdateUser() {
        Mockito
                .when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserServiceImpl.findUserById(2));
        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());

        UserDto mockUserDto = mockUserServiceImpl.updateUser(1L, userUpdateDto);
        assertEquals("Ms. Cesar Funk", mockUserDto.getName());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(any());
    }

    @Test
    void testDeleteUser() {
        Mockito
                .when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserServiceImpl.findUserById(2));
        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());

        UserDto mockUserDto = mockUserServiceImpl.deleteUser(1L);
        assertEquals("Ms. Cesar Funk", mockUserDto.getName());
        Mockito.verify(userRepository, Mockito.times(1))
                .delete(any());
    }

}
