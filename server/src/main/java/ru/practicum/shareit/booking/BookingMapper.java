package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        UserDto bookerDto = UserMapper.toUserDto(booking.getBooker());
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? booking.getItem().getId() : null,
                itemDto,
                bookerDto,
                booking.getStatus().name()
        );
    }

    public static Booking toBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingShortDto(booking.getId(), booking.getBooker().getId());
    }
}