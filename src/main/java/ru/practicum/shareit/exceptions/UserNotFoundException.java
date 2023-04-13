package ru.practicum.shareit.exceptions;

import javax.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long id) {
        super(String.format("Пользователь с id [%d] не найден!", id));
    }
}
