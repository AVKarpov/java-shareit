package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotNull
    private String description;
}
