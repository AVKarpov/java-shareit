package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto addNewBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getAllBookings(Long userId, String state, int from, int size);

    List<BookingResponseDto> getAllBookingsForOwner(Long userId, String state, int from, int size);
}
