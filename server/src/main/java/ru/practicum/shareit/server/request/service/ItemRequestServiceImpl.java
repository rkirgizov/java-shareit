package ru.practicum.shareit.server.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.item.ItemMapper;
import ru.practicum.shareit.server.item.repo.ItemRepository;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.request.Request;
import ru.practicum.shareit.server.request.dto.*;
import ru.practicum.shareit.server.request.repo.ItemRequestRepository;
import ru.practicum.shareit.server.user.repo.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.*;
import static ru.practicum.shareit.server.request.dto.RequestMapper.*;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public RequestDto createItemRequest(long userId, RequestCreateDto requestCreateDto) {
        getUser(userId);
        return toItemRequestDto(itemRequestRepository.save(toItemRequest(userId, requestCreateDto)));
    }

    @Override
    public List<RequestAnswerDto> findAllByOwnerRequests(long userId) {
        getUser(userId);
        List<Request> requestList = itemRequestRepository.findByRequestorOrderByCreatedAsc(userId);
        List<Long> itemRequestIdList = requestList.stream()
                .map(Request::getId)
                .toList();
        List<Item> itemAnswerInterfaceList = itemRepository.findByRequest_idIn(itemRequestIdList);
        Map<Long, List<AnswerDto>> itemAnswerMap = itemAnswerInterfaceList.stream()
                .collect(groupingBy(item -> item.getRequest().getId(),
                        mapping(ItemMapper::toItemAnswer, toList())));
        return requestList.stream()
                .map(RequestMapper::toItemRequestAnswerDto)
                .peek(itemRequestAnswerDto -> {
                    if (Objects.nonNull(itemAnswerMap.get(itemRequestAnswerDto.getId()))) {
                        if (!itemAnswerMap.get(itemRequestAnswerDto.getId()).isEmpty())
                            itemRequestAnswerDto.setItems(itemAnswerMap.get(itemRequestAnswerDto.getId()));
                    }
                })
                .toList();
    }

    @Override
    public List<RequestDto> findAllRequests(long userId) {
        getUser(userId);
        return itemRequestRepository.findByRequestorNotOrderByCreatedAsc(userId).stream()
                .map(RequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public RequestAnswerDto getRequestById(long userId, long requestId) {
        getUser(userId);
        Request request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос с ID %s не найден", requestId)));
        List<AnswerDto> answerDtoList = itemRepository.findByRequest_id(requestId).stream()
                .map(ItemMapper::toItemAnswer)
                .toList();
        if (!answerDtoList.isEmpty()) request.setItems(answerDtoList);
        return toItemRequestAnswerDto(request);
    }

    private void getUser(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

}
