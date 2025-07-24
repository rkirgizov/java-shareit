package ru.practicum.shareit.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.request.controller.ItemRequestController;
import ru.practicum.shareit.server.request.dto.RequestAnswerDto;
import ru.practicum.shareit.server.request.dto.RequestCreateDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 100L;

    private static RequestCreateDto requestCreateDto;
    private static RequestDto requestDto;
    private static LocalDateTime now;
    private static RequestAnswerDto requestAnswerDto;

    @BeforeAll
    static void setup() {
        now = LocalDateTime.now();
        requestCreateDto = RequestCreateDto.builder()
                .description("Need a drill")
                .build();
        requestDto = RequestDto.builder()
                .id(REQUEST_ID)
                .description("Need a drill")
                .created(now)
                .build();
        requestAnswerDto = RequestAnswerDto.builder()
                .id(REQUEST_ID)
                .description("Need a drill")
                .created(now)
                .build();
    }

    @Test
    void testCreateItemRequest_success() throws Exception {
        when(itemRequestService.createItemRequest(USER_ID, requestCreateDto))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(itemRequestService, times(1))
                .createItemRequest(USER_ID, requestCreateDto);
    }

    @Test
    void testCreateItemRequest_userNotFound_throwsNotFoundException() throws Exception {
        when(itemRequestService.createItemRequest(USER_ID, requestCreateDto))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1))
                .createItemRequest(USER_ID, requestCreateDto);
    }

    @Test
    void testFindAllByOwnerRequests_success() throws Exception {
        when(itemRequestService.findAllByOwnerRequests(USER_ID))
                .thenReturn(List.of(requestAnswerDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));

        verify(itemRequestService, times(1)).findAllByOwnerRequests(USER_ID);
    }

    @Test
    void testFindAllRequests_success() throws Exception {
        when(itemRequestService.findAllRequests(USER_ID)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID));

        verify(itemRequestService, times(1)).findAllRequests(USER_ID);
    }

    @Test
    void testGetRequestById_success() throws Exception {
        when(itemRequestService.getRequestById(USER_ID, REQUEST_ID))
                .thenReturn(requestAnswerDto);

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(itemRequestService, times(1))
                .getRequestById(USER_ID, REQUEST_ID);
    }

    @Test
    void testGetRequestById_notFound_throwsNotFoundException() throws Exception {
        when(itemRequestService.getRequestById(USER_ID, REQUEST_ID))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1))
                .getRequestById(USER_ID, REQUEST_ID);
    }

    @Test
    void testFindAllRequests_userNotFound_throwsNotFoundException() throws Exception {
        when(itemRequestService.findAllRequests(USER_ID))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound());
        verify(itemRequestService, times(1)).findAllRequests(USER_ID);
    }
}
