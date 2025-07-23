package ru.practicum.shareit.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.item.ItemCreateDto;
import ru.practicum.shareit.server.request.Request;
import ru.practicum.shareit.server.request.dto.RequestCreateDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.request.dto.RequestAnswerDto;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.repo.UserRepository;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
//@Rollback(false)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServIntegrationTests {

    private final EntityManager em;

    private final ItemRequestService itemRequestService;

    private final UserRepository userRepository;
    private static User user;
    private static User owner;
    private static RequestCreateDto requestCreateDto;
    private static RequestCreateDto otherRequest;
    private static UserCreateDto userCreateDto1;
    private static UserCreateDto userCreateDto2;
    private static ItemCreateDto itemCreateDto;
    private static LocalDateTime now;

    @BeforeAll
    static void modelSetup() {
        now = LocalDateTime.now();
        userCreateDto1 = UserCreateDto.builder()
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();
        userCreateDto2 = UserCreateDto.builder()
                .name("Billie Ryan")
                .email("Citlalli59@hotmail.com")
                .build();

        itemCreateDto = ItemCreateDto.builder()
                .name("Saw")
                .description("Hand saw")
                .available(true)
                .owner(1)
                .build();

        requestCreateDto = RequestCreateDto.builder()
                .description("Need a drill")
                .build();

        otherRequest = RequestCreateDto.builder()
                .description("Another request")
                .build();
    }

    @BeforeEach
    void setup() {
        user = userRepository.save(UserMapper.toUser(userCreateDto1));
        owner = userRepository.save(UserMapper.toUser(userCreateDto2));
    }

    @Test
    void testCreateItemRequest_success() {
        RequestDto requestDto = itemRequestService.createItemRequest(user.getId(), requestCreateDto);

        TypedQuery<Request> query =
                em.createQuery("Select ir from Request ir where ir.description = :description",
                        Request.class);
        Request request = query.setParameter("description", requestDto.getDescription())
                .getSingleResult();

        assertNotNull(requestDto);
        assertNotEquals("Need a hammer", requestDto.getDescription());
        assertNotNull(requestDto.getId());
        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getCreated(), equalTo(requestDto.getCreated()));
    }

    @Test
    void testCreateItemRequest_userNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.createItemRequest(999L, requestCreateDto));
    }

    @Test
    void testFindAllByOwnerRequests_success() {

        RequestDto requestDto = itemRequestService.createItemRequest(user.getId(), requestCreateDto);
        List<RequestDto> requestDtoList = List.of(requestDto);
        List<RequestAnswerDto> result = itemRequestService.findAllByOwnerRequests(user.getId());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().getItems());

        for (RequestDto requestDto1 : requestDtoList) {
            assertThat(result, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(requestDto1.getDescription())),
                    hasProperty("created", equalTo(requestDto1.getCreated()))
            )));
        }

    }

    @Test
    void testFindAllRequests_unsuccess() {

        RequestDto requestDto1 = itemRequestService.createItemRequest(user.getId(), otherRequest);
        RequestDto requestDto2 = itemRequestService.createItemRequest(user.getId(), requestCreateDto);
        List<RequestDto> result = itemRequestService.findAllRequests(owner.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetRequestById_success() {
        RequestDto requestDto2 = itemRequestService.createItemRequest(user.getId(), requestCreateDto);
        RequestAnswerDto result = itemRequestService.getRequestById(user.getId(), requestDto2.getId());

        assertNotNull(result);
        assertEquals(requestDto2.getId(), result.getId());
        assertNotNull(result.getItems());

    }

    @Test
    void testGetRequestById_requestNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(user.getId(), 999L));
    }

}