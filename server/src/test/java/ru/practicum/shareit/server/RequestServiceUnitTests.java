package ru.practicum.shareit.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.item.ItemMapper;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.repo.ItemRepository;
import ru.practicum.shareit.server.request.Request;
import ru.practicum.shareit.server.request.repo.ItemRequestRepository;
import ru.practicum.shareit.server.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.server.request.dto.*;
import ru.practicum.shareit.server.request.dto.RequestMapper;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RequestServiceUnitTests {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 100L;

    private User user;
    private Request request;
    private RequestCreateDto requestCreateDto;
    private Item item;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);

        LocalDateTime now = LocalDateTime.now();

        user = new User();
        user.setId(USER_ID);
        user.setName("Alice");
        user.setEmail("alice@example.com");

//        requestCreateDto = new RequestCreateDto("Need a drill", now);

        requestCreateDto = RequestCreateDto.builder()
                .description("Need a drill")
                .build();

        request = new Request();
        request.setId(REQUEST_ID);
        request.setDescription("Need a drill");
        request.setRequestor(user.getId());
        request.setCreated(now);

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful electric drill");
        item.setAvailable(true);
        item.setRequest(request);
    }

    @Test
    void testCreateItemRequest_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(Request.class))).thenReturn(request);

        RequestDto result = itemRequestService.createItemRequest(USER_ID, requestCreateDto);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        verify(itemRequestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testCreateItemRequest_userNotFound_throwsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.createItemRequest(USER_ID, requestCreateDto));
    }

    @Test
    void testFindAllByOwnerRequests_success() {
        RequestAnswerDto answerDto = RequestMapper.toItemRequestAnswerDto(request);
        answerDto.setItems(List.of(ItemMapper.toItemAnswer(item)));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorOrderByCreatedAsc(USER_ID)).thenReturn(List.of(request));
        when(itemRepository.findByRequest_idIn(List.of(REQUEST_ID))).thenReturn(List.of(item));

        List<RequestAnswerDto> result = itemRequestService.findAllByOwnerRequests(USER_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void testFindAllRequests_success() {
        Request otherRequest = new Request();
        otherRequest.setId(2L);
        otherRequest.setDescription("Another request");
        otherRequest.setRequestor(2L);
        otherRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorNotOrderByCreatedAsc(USER_ID)).thenReturn(List.of(otherRequest));

        List<RequestDto> result = itemRequestService.findAllRequests(USER_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetRequestById_success() {
        AnswerDto answerDto = ItemMapper.toItemAnswer(item);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest_id(REQUEST_ID)).thenReturn(List.of(item));

        RequestAnswerDto result = itemRequestService.getRequestById(USER_ID, REQUEST_ID);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Drill", result.getItems().getFirst().getName());
    }

    @Test
    void testGetRequestById_requestNotFound_throwsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(USER_ID, REQUEST_ID));
    }
}