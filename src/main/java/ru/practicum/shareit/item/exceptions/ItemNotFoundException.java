package ru.practicum.shareit.item.exceptions;

import lombok.Getter;

@Getter
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }

}