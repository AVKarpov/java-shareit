package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    ItemRequestDto itemRequestDto;
    ItemRequestResponseDto itemRequestResponseDto;
    UserDto userDto;
    Long userId;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .description("item request description")
                .build();

        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .description("item request description")
                .build();

        userDto = UserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();

        userId = userService.addUser(userDto).getId();
    }

    @Test
    void addItemRequestReturnItemRequestTest() {
        ItemRequestResponseDto actual = itemRequestService.addItemRequest(userId, itemRequestDto);
        Assertions.assertEquals(itemRequestDto.getDescription(), actual.getDescription(), "Description не совпадают");
    }

    @Test
    void addItemRequestByWrongUserReturnUserNotFoundExceptionTest() {
        assertThrows(UserNotFoundException.class, () -> itemRequestService.addItemRequest(999L, itemRequestDto));
    }

    @Test
    void getAllOwnItemRequestsReturnItemRequestsTest() {
        itemRequestService.addItemRequest(userId, itemRequestDto);
        Assertions.assertEquals(1, itemRequestService.getAllOwnItemRequests(userId).size(), "Количество запросов не совпадает");
    }

    @Test
    void getAllOthersItemRequestsReturnItemRequestsTest() {
        Long otherUserId = userService.addUser(UserDto.builder().name("otherUser").email("otheruser@user.com").build()).getId();
        itemRequestService.addItemRequest(otherUserId, itemRequestDto);
        Assertions.assertEquals(1, itemRequestService.getAllOthersItemRequests(userId, 0 , 5).size(), "Количество запросов не совпадает");
    }

    @Test
    void getRequestByIdReturnItemRequestTest() {
        Long requestId = itemRequestService.addItemRequest(userId, itemRequestDto).getId();
        Assertions.assertEquals(itemRequestResponseDto.getDescription(),
                itemRequestService.getRequestById(userId, requestId).getDescription(), "Запросы не совпадают");
   }

}
