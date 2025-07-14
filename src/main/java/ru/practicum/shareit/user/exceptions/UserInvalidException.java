package ru.practicum.shareit.user.exceptions;

import lombok.Getter;
import ru.practicum.shareit.user.User;

@Getter
public class UserInvalidException extends RuntimeException {
    User user;

    public UserInvalidException(String message, User user) {
        super(message);
        this.user = user;
    }

}