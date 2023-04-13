package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
    }

    public UserDto addUser(UserDto userDto) {
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User existUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        User updatedUser = toUser(userDto);
        if (updatedUser.getName() == null)
            updatedUser.setName(existUser.getName());
        if (updatedUser.getEmail() == null)
            updatedUser.setEmail(existUser.getEmail());
        updatedUser.setId(existUser.getId());
        log.info("Обновлен пользователь с id: {}", existUser.getId());
        return toUserDto(userRepository.save(updatedUser));
    }

    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }
}
