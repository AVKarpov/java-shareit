package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;

    ItemDto itemDto;
    ItemDto itemDto2;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .build();

        itemDto2 = ItemDto.builder()
                .name("item2 name")
                .description("item2 description")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .name("user1")
                .email("user1@user.com")
                .build();
    }

    @Test
    void addNewItemReturnItemTest() {
        Long userId = userService.addUser(userDto).getId();
        ItemDto actualItemDto = itemService.addNewItem(userId, itemDto);

        Assertions.assertEquals(itemDto.getName(), actualItemDto.getName(), "Name не совпадают");
        Assertions.assertEquals(itemDto.getDescription(), actualItemDto.getDescription(), "Description не совпадают");
        Assertions.assertEquals(itemDto.getAvailable(), actualItemDto.getAvailable(), "Available не совпадают");
    }

    @Test
    void getAllItemsTest() {
        Long userId = userService.addUser(userDto).getId();
        itemService.addNewItem(userId, itemDto);
        itemService.addNewItem(userId, itemDto2);
        Assertions.assertEquals(2, itemService.getAllItems(userId, 0, 10).size(),
                "Количество вещей не совпадает");
    }

    @Test
    void getItemByIdReturnItemTest() {
        Long userId = userService.addUser(userDto).getId();
        Long itemId = itemService.addNewItem(userId, itemDto).getId();
        ItemDto actualItemDto = itemService.getItemById(userId, itemId);

        Assertions.assertEquals(itemDto.getName(), actualItemDto.getName(), "Name не совпадают");
        Assertions.assertEquals(itemDto.getDescription(), actualItemDto.getDescription(), "Description не совпадают");
        Assertions.assertEquals(itemDto.getAvailable(), actualItemDto.getAvailable(), "Available не совпадают");
    }

    @Test
    void getItemByIdReturnItemNotFoundExceptionTest() {
        Long userId = userService.addUser(userDto).getId();
        itemService.addNewItem(userId, itemDto);

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(userId, 999L));
    }

    @Test
    void deleteItemTest() {
        Long userId = userService.addUser(userDto).getId();
        Long itemId = itemService.addNewItem(userId, itemDto).getId();

        Assertions.assertEquals(1, itemService.getAllItems(userId, 0, 10).size(),
                "Количество вещей не совпадает");
        itemService.deleteItem(userId, itemId);
        Assertions.assertEquals(0, itemService.getAllItems(userId, 0, 10).size(),
                "Количество вещей не совпадает");
    }

    @Test
    void updateItemReturnUpdatedItemTest() {
        Long userId = userService.addUser(userDto).getId();
        Long itemId = itemService.addNewItem(userId, itemDto).getId();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(itemId)
                .name("item updated name")
                .description("item updated description")
                .available(true)
                .build();

        Assertions.assertEquals(updatedItemDto, itemService.updateItem(userId, itemId, updatedItemDto),
                "Данные вещи не совпадают");
    }

    @Test
    void searchItemsReturnItemsTest() {
        Long userId = userService.addUser(userDto).getId();
        Long itemId = itemService.addNewItem(userId, itemDto).getId();
        itemDto.setId(itemId);
        itemService.addNewItem(userId, itemDto2);
        List<ItemDto> expectedItems = List.of(itemDto);

        Assertions.assertEquals(expectedItems, itemService.searchItems("item1", 0, 10),
                "Данные поиска не совпадают");
    }

    @Test
    void searchItemsReturnEmptyListTest() {
        Long userId = userService.addUser(userDto).getId();
        itemService.addNewItem(userId, itemDto);
        itemService.addNewItem(userId, itemDto2);

        Assertions.assertEquals(Collections.emptyList(), itemService.searchItems("", 0, 10),
                "Данные поиска не совпадают");
    }

    @Test
    void addCommentReturnValidationExceptionTest() {
        Long userId = userService.addUser(userDto).getId();
        Long itemId = itemService.addNewItem(userId, itemDto).getId();
        itemService.addNewItem(userId, itemDto);
        CommentRequestDto comment = CommentRequestDto.builder()
                .text("Test comment")
                .build();
        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, comment));
    }

}
