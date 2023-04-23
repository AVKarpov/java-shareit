package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getAllOwnItemRequests(Long userId);

    List<ItemRequestResponseDto> getAllOthersItemRequests(Long userId, int from, int size);

    ItemRequestResponseDto getRequestById(Long userId, Long requestId);
}
