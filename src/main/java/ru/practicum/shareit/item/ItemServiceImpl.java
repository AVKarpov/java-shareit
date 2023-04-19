package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShortForItem;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public List<ItemDto> getAllItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        List<Item> items = itemRepository.findByOwnerOrderByIdAsc(user);
        List<BookingShortForItem> bookings = bookingRepository.findByItemInAndStatus(items, BookingStatus.APPROVED);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = toItemDto(item);
            itemDto.setLastBooking(findLastBookingForItem(item, bookings));
            itemDto.setNextBooking(findNextBookingForItem(item, bookings));
            itemDto.setComments(getCommentsByItem(item));
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    public ItemDto getItemById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        ItemDto itemDto = toItemDto(item);
        if (userId.equals(item.getOwner().getId())) {
            List<BookingShortForItem> bookings = bookingRepository.findByItemAndStatus(item, BookingStatus.APPROVED);
            itemDto.setLastBooking(findLastBookingForItem(item, bookings));
            itemDto.setNextBooking(findNextBookingForItem(item, bookings));
        }
        itemDto.setComments(getCommentsByItem(item));
        return itemDto;
    }

    private BookingShortForItem findLastBookingForItem(Item item, List<BookingShortForItem> bookings) {
        return bookings
                .stream()
                .filter(b -> b.getItemId().equals(item.getId()))
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingShortForItem::getStart))
                .orElse(null);
    }

    private BookingShortForItem findNextBookingForItem(Item item, List<BookingShortForItem> bookings) {
        return bookings
                .stream()
                .filter(b -> b.getItemId().equals(item.getId()))
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingShortForItem::getStart))
                .orElse(null);
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = toItem(itemDto);
        item.setOwner(user);
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long id, Long itemId, ItemDto itemDto) {
        Item existItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        if (existItem.getOwner() != null && !id.equals(existItem.getOwner().getId()))
            throw new EntityNotFoundException("Ошибка обновления информации о вещи с id = " + existItem.getId());
        Item updatedItem = toItem(itemDto);
        if (updatedItem.getId() == null)
            updatedItem.setId(existItem.getId());
        if (updatedItem.getName() == null)
            updatedItem.setName(existItem.getName());
        if (updatedItem.getDescription() == null)
            updatedItem.setDescription(existItem.getDescription());
        if (updatedItem.getIsAvailable() == null)
            updatedItem.setIsAvailable(existItem.getIsAvailable());
        if (updatedItem.getOwner() == null)
            updatedItem.setOwner(existItem.getOwner());
        return toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item existItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        if (existItem.getOwner() != null && !userId.equals(existItem.getOwner().getId()))
            throw new EntityNotFoundException("Ошибка удаления вещи с id = " + existItem.getId()
                    + " пользователем с id = " + userId);
        itemRepository.deleteById(existItem.getId());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank())
            return Collections.emptyList();
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        BookingShortForItem booking = bookingRepository.findFirstByItemAndBookerAndStatus(item, user, BookingStatus.APPROVED);
        if (booking == null)
            throw new ValidationException("Нельзя оставить комментарий у вещи, которую не бронировал!");
        else if (booking.getEnd().isAfter(LocalDateTime.now()))
            throw new ValidationException("Срок аренды ещё не завершился!");
        Comment comment = Comment.builder()
                .item(item)
                .text(text.getText())
                .author(user)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        return toCommentDto(comment);
    }

    private List<CommentResponseDto> getCommentsByItem(Item item) {
        return commentRepository.findByItem(item)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
