package ru.practicum.shareit.booking.enumeration;

public enum State {
    ALL,  // Все бронирования
    CURRENT,  // Текущие бронирования
    PAST,  // Прошедшие бронирования
    FUTURE,  // Будущие бронирования
    WAITING,  // Ожидающие подтверждения
    REJECTED  // Отклоненные бронирования
}