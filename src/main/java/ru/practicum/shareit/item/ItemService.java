package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long id, Long itemId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId);

    List<ItemDto> searchItems(String text);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto text);
}
