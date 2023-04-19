package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingShortForItem {
    Long id;
    Long bookerId;
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;
}
