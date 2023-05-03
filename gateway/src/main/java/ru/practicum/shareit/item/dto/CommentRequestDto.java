package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotBlank
    private String text;
}
