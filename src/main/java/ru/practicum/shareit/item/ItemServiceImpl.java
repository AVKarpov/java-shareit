package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAllItems()
                .stream()
                .filter(u -> u.getOwner() != null && u.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long itemId) {
        return toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User user = userStorage.getUserById(userId);
        Item item = toItem(itemDto);
        item.setOwner(user);
        return toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existItem = itemStorage.getItemById(itemId);
        if (existItem.getOwner() != null && !userId.equals(existItem.getOwner().getId()))
            throw new NoSuchElementException("Ошибка обновления информации о вещи с id = " + itemId);

        Item updatedItem = toItem(itemDto);
        if (updatedItem.getId() == null)
            updatedItem.setId(itemId);
        if (updatedItem.getName() == null)
            updatedItem.setName(existItem.getName());
        if (updatedItem.getDescription() == null)
            updatedItem.setDescription(existItem.getDescription());
        if (updatedItem.getAvailable() == null)
            updatedItem.setAvailable(existItem.getAvailable());
        if (updatedItem.getOwner() == null)
            updatedItem.setOwner(existItem.getOwner());

        return toItemDto(itemStorage.updateItem(updatedItem));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item existItem = itemStorage.getItemById(itemId);
        if (existItem.getOwner() != null && !userId.equals(existItem.getOwner().getId()))
            throw new NoSuchElementException("Ошибка удаления вещи с id = " + itemId + " пользователем с id = " + userId);
        itemStorage.removeItem(itemId);
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty())
            return Collections.emptyList();
        return itemStorage.searchItems((text.toLowerCase())).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

}
