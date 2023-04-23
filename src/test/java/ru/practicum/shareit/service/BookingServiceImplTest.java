package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    ItemDto itemDto;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .name("user1")
                .email("user1@user.com")
                .build();
    }

    @Test
    void addNewBookingReturnBookingTest() {
        Long ownerId = userService.addUser(userDto).getId();
        Long bookerId = userService.addUser(UserDto.builder().name("user2").email("user2@test.com").build()).getId();
        Long itemId = itemService.addNewItem(ownerId, itemDto).getId();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingResponseDto bookingResponseDto = bookingService.addNewBooking(bookerId, bookingRequestDto);

        Assertions.assertEquals(bookingRequestDto.getItemId(), bookingResponseDto.getItem().getId(), "Item id не совпадают");
        Assertions.assertEquals(bookingRequestDto.getStart(), bookingResponseDto.getStart(), "Start не совпадают");
        Assertions.assertEquals(bookingRequestDto.getEnd(), bookingResponseDto.getEnd(), "End не совпадают");
    }

    @Test
    void addNewBookingReturnEntityNotFoundExTest() {
        Long ownerId = userService.addUser(userDto).getId();
        Long itemId = itemService.addNewItem(ownerId, itemDto).getId();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(EntityNotFoundException.class, () -> bookingService.addNewBooking(ownerId, bookingRequestDto));
    }

    @Test
    void addNewBookingItemAvailableFalseReturnValidationExTest() {
        Long ownerId = userService.addUser(userDto).getId();
        Long bookerId = userService.addUser(UserDto.builder().name("user2").email("user2@test.com").build()).getId();
        itemDto.setAvailable(false);
        Long itemId = itemService.addNewItem(ownerId, itemDto).getId();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(bookerId, bookingRequestDto));
    }

    @Test
    void addNewBookingIncorrectBookingDateReturnValidationExTest() {
        Long ownerId = userService.addUser(userDto).getId();
        Long bookerId = userService.addUser(UserDto.builder().name("user2").email("user2@test.com").build()).getId();
        Long itemId = itemService.addNewItem(ownerId, itemDto).getId();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().minusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(bookerId, bookingRequestDto));
    }

    @Test
    void approveBookingReturnBookingStatusTest() {
        Long ownerId = userService.addUser(userDto).getId();
        Long bookerId = userService.addUser(UserDto.builder().name("user2").email("user2@test.com").build()).getId();
        Long itemId = itemService.addNewItem(ownerId, itemDto).getId();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Long bookingId = bookingService.addNewBooking(bookerId, bookingRequestDto).getId();
        BookingResponseDto bookingResponseDto = bookingService.approveBooking(ownerId, bookingId, true);

        Assertions.assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus(), "Status не совпадают");
    }

    @Test
    void getBookingByIdReturnBookingTest() {
        Long ownerId = userService.addUser(userDto).getId();
        Long bookerId = userService.addUser(UserDto.builder().name("user2").email("user2@test.com").build()).getId();
        Long itemId = itemService.addNewItem(ownerId, itemDto).getId();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Long bookingId = bookingService.addNewBooking(bookerId, bookingRequestDto).getId();

        Assertions.assertEquals(bookingRequestDto.getItemId(),
                bookingService.getBookingById(ownerId, bookingId).getItem().getId(), "Booking не совпадают");
    }

    @Test
    void getAllBookingsReturnBookingsTest() {
        Long ownerId = userService.addUser(userDto).getId();
        Long bookerId = userService.addUser(UserDto.builder().name("user2").email("user2@test.com").build()).getId();
        Long itemId = itemService.addNewItem(ownerId, itemDto).getId();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.addNewBooking(bookerId, bookingRequestDto);

        Assertions.assertEquals(1,
                bookingService.getAllBookings(bookerId, "FUTURE", 0, 10).size(), "Количество не совпадает");
    }

}
