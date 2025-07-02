package ru.practicum.shareit.user.exceptions;

import lombok.Getter;
import ru.practicum.shareit.user.User;

@Getter
public class UserInvalidEmailNotFoundException extends RuntimeException {
    User user;

    public UserInvalidEmailNotFoundException(String message, User user) {
        super(message);
        this.user = user;
    }

    public UserInvalidEmailNotFoundException(String message) {
        super(message);
    }
}