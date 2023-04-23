package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    //POST /requests - добавление нового запроса вещи
    @PostMapping
    public ItemRequestResponseDto addItemRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                                 @RequestBody @Validated ItemRequestDto itemRequestDto) {
        return requestService.addItemRequest(userId, itemRequestDto);
    }

    //GET /requests - получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestResponseDto> getAllOwnItemRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        return requestService.getAllOwnItemRequests(userId);
    }

    //GET /requests/all?from={from}&size={size} - получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllOthersItemRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        return requestService.getAllOthersItemRequests(userId, from, size);
    }

    //GET /requests/{requestId} - получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping(value = "/{requestId}")
    public ItemRequestResponseDto getItemById(@RequestHeader(HEADER_USER_ID) Long userId,
                                              @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

}
