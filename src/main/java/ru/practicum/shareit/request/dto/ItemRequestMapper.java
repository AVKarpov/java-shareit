package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, List<ItemDto> itemDtos) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtos)
                .build();
    }

    public static List<ItemRequestResponseDto> toItemRequestResponseDto(List<ItemRequest> itemRequests, List<ItemDto> itemDtos) {
        List<ItemRequestResponseDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtos.add(toItemRequestResponseDto(itemRequest,
                    itemDtos.stream().filter(item -> item.getRequestId().equals(itemRequest.getId())).collect(Collectors.toList())
            ));
        }
        return dtos;
    }
}
