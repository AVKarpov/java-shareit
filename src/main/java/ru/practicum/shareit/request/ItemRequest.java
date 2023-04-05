package ru.practicum.shareit.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;     //текст запроса, содержащий описание требуемой вещи
    private User requestor;         //пользователь, создавший запрос
    private LocalDateTime created;  //дата и время создания запроса
}
