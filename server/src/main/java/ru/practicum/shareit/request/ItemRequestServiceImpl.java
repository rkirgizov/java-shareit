package ru.practicum.shareit.request;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));

        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(saved);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
                    dto.setItems(itemRepository.findByRequestId(request.getId())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));

        Pageable pageable = PageRequest.of(from / size, size);

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageable);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
                    dto.setItems(itemRepository.findByRequestId(request.getId())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found with id=" + requestId));

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
        dto.setItems(itemRepository.findByRequestId(request.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        List<ItemRequest> requests = itemRequestRepository.findAllByOrderByCreatedDesc();

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
                    dto.setItems(itemRepository.findByRequestId(request.getId())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}