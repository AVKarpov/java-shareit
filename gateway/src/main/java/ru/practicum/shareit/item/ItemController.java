package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

	private final ItemClient itemClient;
	private static final String HEADER_USER_ID = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getAllItems(@RequestHeader(HEADER_USER_ID) Long userId,
									 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
									 @RequestParam(defaultValue = "10") @Positive int size) {
		log.info("Get all items userId={}, from={}, size={}", userId, from, size);
		return itemClient.getAllItems(userId, from, size);
	}

	@GetMapping(value = "/{itemId}")
	public ResponseEntity<Object> getItemById(@RequestHeader(HEADER_USER_ID) Long userId,
											  @PathVariable Long itemId) {
		log.info("Get item with id={}, userId={}", itemId, userId);
		return itemClient.getItemById(userId, itemId);
	}

	@PostMapping
	public ResponseEntity<Object> addItem(@RequestHeader(HEADER_USER_ID) Long userId,
										  @RequestBody @Validated ItemDto itemDto) {
		log.info("Add new item {}, userId={}", itemDto, userId);
		return itemClient.addItem(userId, itemDto);
	}

	@PatchMapping(value = "/{itemId}")
	public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
											 @PathVariable Long itemId,
											 @RequestBody ItemDto itemDto) {
		log.info("Update item with id={} by userId={}. Updated item: {}", itemId, userId, itemDto);
		return itemClient.updateItem(userId, itemId, itemDto);
	}

	@DeleteMapping("/{itemId}")
	public ResponseEntity<Object> deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
											 @PathVariable Long itemId) {
		log.info("Delete item={} by userId={}", itemId, userId);
		return itemClient.deleteItem(userId, itemId);
	}

	@GetMapping("search")
	public ResponseEntity<Object> searchItems(@RequestHeader(HEADER_USER_ID) Long userId,
											  @RequestParam String text,
											  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
											  @RequestParam(defaultValue = "10") @Positive int size) {
		log.info("Search items by text={}, userId={}, from={}, size={}", text, userId, from, size);
		return itemClient.searchItems(userId, text, from, size);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> addComment(@RequestHeader(HEADER_USER_ID) Long userId,
											 @PathVariable Long itemId,
											 @RequestBody @Validated CommentRequestDto text) {
		log.info("Add comment={} to itemId={} by userId={}", text, itemId, userId);
		return itemClient.addComment(userId, itemId, text);
	}

}
