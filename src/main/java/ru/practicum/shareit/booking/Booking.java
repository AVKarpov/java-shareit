package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime start; //дата и время начала бронирования
    private LocalDateTime end; //дата и время конца бронирования
    private Item item; //вещь, которую пользователь бронирует
    private User booker; //пользователь, который осуществляет бронирование
    private BookingStatus status; //статус бронирования
}
