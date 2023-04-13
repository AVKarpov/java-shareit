package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingResponseDto addNewBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(bookingRequestDto.getItemId()));
        if (item.getOwner() != null && userId.equals(item.getOwner().getId()))
            throw new EntityNotFoundException("Нельзя забронировать свою вешь!");
        if (Boolean.FALSE.equals(item.getIsAvailable()))
            throw new ValidationException(String.format("Вещь с id [%d] недоступна!", item.getId()));
        if (bookingRequestDto.getStart() == null ||
                bookingRequestDto.getEnd() == null ||
                bookingRequestDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingRequestDto.getEnd().isBefore(LocalDateTime.now()) ||
                bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart()) ||
                bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd()))
            throw new ValidationException("Даты бронирования указаны некорректно!");

        Booking booking = toBooking(bookingRequestDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Бронирование с id [%d] не найдено!", bookingId)));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId))
             throw new EntityNotFoundException(
                     String.format("Изменение статуса бронирования для пользователя с id [%d] запрещено!", userId));

        if ((isApproved && booking.getStatus().equals(BookingStatus.APPROVED)) ||
                (!isApproved && booking.getStatus().equals(BookingStatus.REJECTED)))
            throw new ValidationException("Статусы бронирования совпадают!");

        booking.setStatus(Boolean.TRUE.equals(isApproved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Бронирование с id [%d] не найдено!", bookingId)));
        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId))
            throw new EntityNotFoundException("Доступ к информации о бронировании запрещён!");
        return toBookingDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        List<Booking> bookings;
        switch (parseStatus(state)) {
            case PAST:
                bookings = new ArrayList<>(
                        bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                );
                break;

            case FUTURE:
                bookings = new ArrayList<>(
                        bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                );
                break;

            case CURRENT:
                bookings = new ArrayList<>(
                        bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(userId,
                                LocalDateTime.now(), LocalDateTime.now())
                );
                break;

            case WAITING:
            case REJECTED:
                bookings = new ArrayList<>(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.valueOf(state))
                );
                break;

            default: //ALL
                bookings = new ArrayList<>(bookingRepository.findByBookerIdOrderByStartDesc(userId));
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsForOwner(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        List<Item> items = itemRepository.findByOwnerOrderByIdAsc(user);
        List<Booking> bookings = new ArrayList<>();
        switch (parseStatus(state)) {
            case PAST:
                    bookings.addAll(bookingRepository.findByItemInAndEndIsBeforeOrderByStartDesc(items, LocalDateTime.now()));
                break;
            case FUTURE:
                    bookings.addAll(bookingRepository.findByItemInAndStartIsAfterOrderByStartDesc(items, LocalDateTime.now()));
                break;

            case CURRENT:
                    bookings.addAll(bookingRepository.findByItemInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(items,
                            LocalDateTime.now(), LocalDateTime.now()));
                break;

            case WAITING:
            case REJECTED:
                    bookings.addAll(bookingRepository.findByItemInAndStatusOrderByStartDesc(items,
                                BookingStatus.valueOf(state)));
                break;
            default: //ALL
                    bookings.addAll(bookingRepository.findByItemInOrderByStartDesc(items));
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private BookingStatusDto parseStatus(String state) {
        try {
            return BookingStatusDto.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }
}
