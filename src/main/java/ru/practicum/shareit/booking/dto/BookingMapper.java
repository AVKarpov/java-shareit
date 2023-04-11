package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

public class BookingMapper {

    private BookingMapper() {

    }

    public static BookingResponseDto toBookingDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(toItemDto(booking.getItem()))
                .booker(toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }

}