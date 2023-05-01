package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    //GET /items?from={from}&size={size} - получение всех вещей пользователя
    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(HEADER_USER_ID) Long userId,
                                     @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10", required = false) @Positive int size) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItemById(@RequestHeader(HEADER_USER_ID) Long userId,
                               @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    //POST /items - добавление новой вещи
    @PostMapping
    public ItemDto addNewItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @RequestBody @Validated ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    //PATCH /items/{itemId} - редактирование вещи
    @PatchMapping(value = "/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    //DELETE /items/{itemId} - удаление вещи
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    //GET /items/search?text={text}&from={from}&size={size}
    @GetMapping("search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10", required = false) @Positive int size) {
        return itemService.searchItems(text, from, size);
    }

    //POST /items/{itemId}/comment - добавить комментарий
    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody @Validated CommentRequestDto text) {
        return itemService.addComment(userId, itemId, text);
    }

}
