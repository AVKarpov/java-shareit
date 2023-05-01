package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    //POST /bookings - добавление запроса на бронирование
    @PostMapping
    public BookingResponseDto addNewBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.addNewBooking(userId, bookingRequestDto);
    }

    //PATCH /bookings/{bookingId}?approved={approved} - подтверждение или отклонение запроса на бронирование
    @PatchMapping(value = "/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    //GET /bookings/{bookingId} - получение данных о конкретном бронировании
    @GetMapping(value = "/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    //GET /bookings?state={state}&from={from}&size={size} - получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<BookingResponseDto> getAllBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "ALL", required = false) String state,
                                                   @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10", required = false) @Positive int size) {
        return bookingService.getAllBookings(userId, state, from, size);
    }

    //GET /bookings/owner?state={state}&from={from}&size={size} - получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsForAllItems(@RequestHeader(HEADER_USER_ID) Long userId,
                                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                                              @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                                              @RequestParam(defaultValue = "10", required = false) @Positive int size) {
        return bookingService.getAllBookingsForOwner(userId, state, from, size);
    }
}
