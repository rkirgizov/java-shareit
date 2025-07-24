package ru.practicum.shareit.gateway.common;

import jakarta.validation.ValidationException;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;

import java.util.Objects;

public class GatewayCheckUtility {

    public static void isItemUpdateDto(ItemUpdateDto itemUpdateDto) {
        if (Objects.nonNull(itemUpdateDto.getName())
                && (itemUpdateDto.getName().isBlank() || itemUpdateDto.getName().isEmpty()))
            throw new ValidationException(
                    String.format("Вещь %s не прошла валидацию имени при обновлении",
                            itemUpdateDto));
        if (Objects.nonNull(itemUpdateDto.getDescription())
                && (itemUpdateDto.getDescription().isEmpty() || itemUpdateDto.getDescription().isBlank()))
            throw new ValidationException(
                    String.format("Вещь %s не прошла валидацию описания при обновлении", itemUpdateDto));
    }

    public static void isStringQuery(String searchQuery) {
        if (Objects.isNull(searchQuery))
            throw new ValidationException(String.format("Запрос %s не прошел валидацию", searchQuery));

    }

    public static void isUserUpdateDto(UserUpdateDto userUpdateDto) {
        if (Objects.nonNull(userUpdateDto.getName())
                && (userUpdateDto.getName().isBlank() || userUpdateDto.getName().isEmpty()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию имени",
                    userUpdateDto));
        if (Objects.nonNull(userUpdateDto.getEmail())
                && (userUpdateDto.getEmail().isEmpty() || userUpdateDto.getEmail().isBlank()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию почты",
                    userUpdateDto));
    }

    public static void isStartEndValid(NewBookingDto newBookingDto) {
        if (newBookingDto.getStart().isAfter(newBookingDto.getEnd())
                || newBookingDto.getStart().isEqual(newBookingDto.getEnd()))
            throw new ValidationException(String.format("Неверно указаны даты аренды." +
                            "Дата начала аренды %s наступает после или одновременно с датой конца %s аренды",
                    newBookingDto.getStart(), newBookingDto.getEnd()));
    }

}
