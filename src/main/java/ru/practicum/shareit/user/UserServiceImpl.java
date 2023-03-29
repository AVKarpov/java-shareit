package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return toUserDto(userStorage.getUserById(id));
    }

    public UserDto addUser(UserDto userDto) {
        User user = toUser(userDto);
        if (!isValidUser(user))
            throw new ValidationException("Ошибка валидации пользователя");
        return toUserDto(userStorage.addUser(user));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User updatedUser = toUser(userDto);
        updatedUser.setId(id);
        if (!isValidUser(updatedUser))
            throw new ValidationException("Ошибка валидации пользователя");

        return toUserDto(userStorage.updateUser(updatedUser));
    }

    public void removeUser(Long id) {
        userStorage.removeUser(id);
    }

    private boolean isValidUser(User user) {
        return userStorage.isValidUser(user);
    }

}
