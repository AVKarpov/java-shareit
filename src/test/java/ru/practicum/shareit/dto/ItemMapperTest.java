package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

class ItemMapperTest {

    @Test
    void toItemDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .name("Test item name")
                .description("Test item description")
                .isAvailable(true)
                .request(ItemRequest.builder().id(1L).build())
                .build();
        ItemDto itemDto = toItemDto(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getIsAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void toItemListDtoTest() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test item1 name")
                .description("Test item1 description")
                .isAvailable(true)
                .request(ItemRequest.builder().id(1L).build())
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Test item2 name")
                .description("Test item2 description")
                .isAvailable(true)
                .request(ItemRequest.builder().id(2L).build())
                .build();
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        List<ItemDto> itemDtos = toItemDto(items);
        assertEquals(item1.getId(), itemDtos.get(0).getId());
        assertEquals(item1.getName(), itemDtos.get(0).getName());
        assertEquals(item1.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(item1.getIsAvailable(), itemDtos.get(0).getAvailable());
        assertEquals(item1.getRequest().getId(), itemDtos.get(0).getRequestId());

        assertEquals(item2.getId(), itemDtos.get(1).getId());
        assertEquals(item2.getName(), itemDtos.get(1).getName());
        assertEquals(item2.getDescription(), itemDtos.get(1).getDescription());
        assertEquals(item2.getIsAvailable(), itemDtos.get(1).getAvailable());
        assertEquals(item2.getRequest().getId(), itemDtos.get(1).getRequestId());
    }

    @Test
    void toItemTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test item name")
                .description("Test item description")
                .build();
        Item item = toItem(itemDto);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
    }

}
