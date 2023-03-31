package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long generatorId = 0;

    @Override
    public List<Item> getAllItems() {
        log.info("Текущее количество вещей: {}", items.size());
        return new ArrayList<>(items.values());
    }

    @Override
    public Item getItemById(Long itemId) {
        if (items.get(itemId) == null)
            throw new NoSuchElementException("Вещь с id = " + itemId + " не найдена");
        return items.get(itemId);
    }

    @Override
    public Item addItem(Item item) {
        item.setId(++generatorId);
        items.put(item.getId(), item);
        log.info("Добавлена вещь: {}", item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.replace(item.getId(), item);
        log.info("Обновлена информация по вещи с id: {}", item);
        return item;
    }

    @Override
    public void removeItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.info("Невозможно удалить вещь! Вещь с таким id не найдена: {}", itemId);
            throw new NoSuchElementException("Невозможно удалить вещь! Вещь с таким id не найдена");
        }
        items.remove(itemId);
    }

    public List<Item> searchItems(String text) {
        return  items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

}
