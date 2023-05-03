package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingShortForItem;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingShortForItem lastBooking;
    private BookingShortForItem nextBooking;
    private List<CommentResponseDto> comments;
}