package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequestResponseDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestResponseDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return toItemRequestResponseDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public List<ItemRequestResponseDto> getAllOwnItemRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorOrderByCreatedDesc(user);
        List<Item> items = itemRepository.findByRequestIdIn(
                itemRequests
                        .stream()
                        .map(ItemRequest::getId).collect(Collectors.toList())
                        );
        return toItemRequestResponseDto(itemRequests, toItemDto(items));
    }

    public List<ItemRequestResponseDto> getAllOthersItemRequests(Long userId, int from, int size) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorNot(requestor,
                PageRequest.of(from/size, size, Sort.by(Sort.Direction.DESC, "created")));
        if (!itemRequests.isEmpty()) {
            List<Item> items = itemRepository.findByRequestIdIn(
                    itemRequests
                            .stream()
                            .map(ItemRequest::getId).collect(Collectors.toList())
            );
            return toItemRequestResponseDto(itemRequests, toItemDto(items));
        }
        return Collections.emptyList();
    }

    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        return toItemRequestResponseDto(itemRequest, toItemDto(items));
    }

}
