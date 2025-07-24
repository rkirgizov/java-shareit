package ru.practicum.shareit.server.common;

import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.Item;

public class ServerCheckUtility {

    public static void isOwner(long userId, long ownerId) {
        if (userId != ownerId)
            throw new ValidationException(
                    String.format("Пользователь с ID %s не является владельцем вещи c ID влдаельца %s",
                            userId, ownerId));
    }

    public static boolean isOwnerBoolean(long userId, long ownerId) {
       return userId == ownerId;
    }

    public static void isItemAvailable(Item item) {
        if (item.getAvailable().equals(false))
            throw new ValidationException(String.format("Вещь %s для заказа недоступна", item.getId()));
    }

    public static void isBookerOrOwner(long userId, long bookerId, long ownerId) {
        if ((userId != bookerId) && (userId != ownerId))
            throw new ValidationException(
                    String.format("Пользователь с ID %s не является ни автором бронирования (ID %s), " +
                                    "ни владельцем вещи (ID %s)",
                            userId, bookerId, ownerId));
    }

}
