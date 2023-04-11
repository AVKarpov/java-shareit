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
    List<Booking> findByItemOrderByStartDesc(Item item);
    List<Booking> findByItemAndEndIsBeforeOrderByStartDesc(Item item, LocalDateTime end);
    List<Booking> findByItemAndStartIsAfterOrderByStartDesc(Item item, LocalDateTime start);
    List<Booking> findByItemAndStatusOrderByStartDesc(Item item, BookingStatus status);
    List<Booking> findByItemAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Item item, LocalDateTime start,
                                                                          LocalDateTime end);
    BookingShortForItem findFirstByItemAndStartIsBeforeAndStatusOrderByStartDesc(Item item, LocalDateTime end,
                                                                                 BookingStatus status);
    BookingShortForItem findFirstByItemAndStartIsAfterAndStatusOrderByStartAsc(Item item, LocalDateTime start,
                                                                               BookingStatus status);
    BookingShortForItem findFirstByItemAndBookerAndStatus(Item item, User booker, BookingStatus status);
}