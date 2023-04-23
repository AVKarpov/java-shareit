package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingShortForItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDto {
    private Long id;
    @NotNull @NotBlank
    private String name;
    @NotNull @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingShortForItem lastBooking;
    private BookingShortForItem nextBooking;
    private List<CommentResponseDto> comments;
}
