package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
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

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    ItemDto itemDto;
    UserDto userDto;
    Long ownerId;
    Long bookerId;
    Long itemId;
    BookingRequestDto bookingRequestDto;
    Long bookingId;


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

        ownerId = userService.addUser(userDto).getId();
        bookerId = userService.addUser(UserDto.builder().name("user2").email("user2@test.com").build()).getId();
        itemId = itemService.addNewItem(ownerId, itemDto).getId();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingId = bookingService.addNewBooking(bookerId, bookingRequestDto).getId();
    }

    @Test
    void addNewBookingReturnBookingTest() {
        Long ownerId = userService.addUser(UserDto.builder().name("user").email("user@user.com").build()).getId();
        Long bookerId = userService.addUser(UserDto.builder().name("user3").email("user3@test.com").build()).getId();
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
    void addNewBookingByOwnerReturnEntityNotFoundExTest() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.addNewBooking(ownerId, bookingRequestDto));
    }

    @Test
    void addNewBookingItemAvailableFalseReturnValidationExTest() {
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
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().minusMinutes(20))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(bookerId, bookingRequestDto));
    }

    @Test
    void approveBookingReturnBookingStatusTest() {
        BookingResponseDto bookingResponseDto = bookingService.approveBooking(ownerId, bookingId, true);

        Assertions.assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus(), "Status не совпадают");
    }

    @Test
    void approveBookingWithWrongBookingIdReturnEntityNotFoundExceptionTest() {
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(ownerId, 999L, true));
    }

    @Test
    void approveBookingByNotOwnerReturnEntityNotFoundExceptionTest() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.approveBooking(bookerId, bookingId, true));
    }

    @Test
    void getBookingByIdReturnBookingTest() {
        Assertions.assertEquals(bookingRequestDto.getItemId(),
                bookingService.getBookingById(ownerId, bookingId).getItem().getId(), "Booking не совпадают");
    }

    @Test
    void getBookingByIdWithWrongBookingIdReturnEntityNotFoundExceptionTest() {
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(ownerId, 999L));
    }

    @Test
    void getAllBookingsWithFutureStatusReturnFutureBookingsTest() {
        Assertions.assertEquals(1,
                bookingService.getAllBookings(bookerId, "FUTURE", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsWithPastStatusReturnPastBookingsTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(20))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.APPROVED)
                .item(itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemNotFoundException(itemId)))
                .booker(userRepository.findById(bookerId)
                        .orElseThrow(() -> new UserNotFoundException(bookerId)))
                .build();
        bookingRepository.save(booking);

        Assertions.assertEquals(1,
                bookingService.getAllBookings(bookerId, "PAST", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsWithCurrentStatusReturnCurrentBookingsTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(20))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .item(itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemNotFoundException(bookingRequestDto.getItemId())))
                .booker(userRepository.findById(bookerId)
                        .orElseThrow(() -> new UserNotFoundException(bookerId)))
                .build();
        bookingRepository.save(booking);

        Assertions.assertEquals(1,
                bookingService.getAllBookings(bookerId, "CURRENT", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsWithRejectedStatusReturnRejectedBookingsTest() {
        bookingService.approveBooking(ownerId, bookingId, false);
        Assertions.assertEquals(1,
                bookingService.getAllBookings(bookerId, "REJECTED", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsWithAllStatusReturnAllBookingsTest() {
        Assertions.assertEquals(1,
                bookingService.getAllBookings(bookerId, "ALL", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsWithUnknownStatusReturnValidationExceptionTest() {
        assertThrows(ValidationException.class, () -> bookingService.getAllBookings(bookerId, "UNKNOWN", 0, 10));
    }

    @Test
    void getAllBookingsForOwnerWithFutureStatusReturnFutureBookingsTest() {
        Assertions.assertEquals(1,
                bookingService.getAllBookingsForOwner(ownerId, "FUTURE", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsForOwnerWithPastStatusReturnPastBookingsTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(20))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.APPROVED)
                .item(itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemNotFoundException(bookingRequestDto.getItemId())))
                .booker(userRepository.findById(bookerId)
                        .orElseThrow(() -> new UserNotFoundException(bookerId)))
                .build();
        bookingRepository.save(booking);

        Assertions.assertEquals(1,
                bookingService.getAllBookingsForOwner(ownerId, "PAST", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsForOwnerWithCurrentStatusReturnCurrentBookingsTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(20))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .item(itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemNotFoundException(bookingRequestDto.getItemId())))
                .booker(userRepository.findById(bookerId)
                        .orElseThrow(() -> new UserNotFoundException(bookerId)))
                .build();
        bookingRepository.save(booking);

        Assertions.assertEquals(1,
                bookingService.getAllBookingsForOwner(ownerId, "CURRENT", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsForOwnerWithRejectedStatusReturnRejectedBookingsTest() {
        bookingService.approveBooking(ownerId, bookingId, false);
        Assertions.assertEquals(1,
                bookingService.getAllBookingsForOwner(ownerId, "REJECTED", 0, 10).size(), "Количество не совпадает");
    }

    @Test
    void getAllBookingsForOwnerWithAllStatusReturnAllBookingsTest() {
        Assertions.assertEquals(1,
                bookingService.getAllBookingsForOwner(ownerId, "ALL", 0, 10).size(), "Количество не совпадает");
    }

}
