package ru.practicum.shareit.server.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.user.User;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserCreateDto userCreateDto) {
        User user = new User();
        user.setId(userCreateDto.getId());
        user.setName(userCreateDto.getName());
        user.setEmail(userCreateDto.getEmail());
        return user;
    }

    public static User toUser(User user, UserUpdateDto userUpdateDto) {
        if (Objects.nonNull(userUpdateDto.getName())) user.setName(userUpdateDto.getName());
        if (Objects.nonNull(userUpdateDto.getEmail())) user.setEmail(userUpdateDto.getEmail());
        return user;
    }


}
