package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllItems();

    Item getItemById(Long id);

    Item addItem(Item item);

    Item updateItem(Item item);

    void removeItem(Long id);

    List<Item> searchItems(String text);
}
