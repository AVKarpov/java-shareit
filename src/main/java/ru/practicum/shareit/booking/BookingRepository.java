package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(Long bookerId, LocalDateTime start,
                                                                              LocalDateTime end, Pageable pageable);

    List<Booking> findByItemInOrderByStartDesc(List<Item> items, Pageable pageable);

    List<Booking> findByItemInAndEndIsBeforeOrderByStartDesc(List<Item> items, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemInAndStartIsAfterOrderByStartDesc(List<Item> items, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatus status, Pageable pageable);

    List<Booking> findByItemInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(List<Item> items, LocalDateTime start,
                                                                          LocalDateTime end, Pageable pageable);

    List<BookingShortForItem> findByItemInAndStatus(List<Item> items, BookingStatus status);

    List<BookingShortForItem> findByItemAndStatus(Item item, BookingStatus status);

    BookingShortForItem findFirstByItemAndBookerAndStatus(Item item, User booker, BookingStatus status);
}