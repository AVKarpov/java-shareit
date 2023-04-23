package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

class UserMapperTest {

    @Test
    void toUserDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("Test user name")
                .email("test@test.com")
                .build();
        UserDto userDto = toUserDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Test user name")
                .email("test@test.com")
                .build();
        User user = toUser(userDto);
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

}
