package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(Long bookerId, LocalDateTime start,
                                                                              LocalDateTime end);

    List<Booking> findByItemInOrderByStartDesc(List<Item> items);

    List<Booking> findByItemInAndEndIsBeforeOrderByStartDesc(List<Item> items, LocalDateTime end);

    List<Booking> findByItemInAndStartIsAfterOrderByStartDesc(List<Item> items, LocalDateTime start);

    List<Booking> findByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatus status);

    List<Booking> findByItemInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(List<Item> items, LocalDateTime start,
                                                                          LocalDateTime end);

    List<BookingShortForItem> findByItemInAndStatus(List<Item> items, BookingStatus status);

    List<BookingShortForItem> findByItemAndStatus(Item item, BookingStatus status);

    BookingShortForItem findFirstByItemAndBookerAndStatus(Item item, User booker, BookingStatus status);
}