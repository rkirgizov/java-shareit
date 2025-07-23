package ru.practicum.shareit.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.User;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;

@Transactional
//@Rollback(false)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private static UserCreateDto userCreateDto1;
    private static UserCreateDto userCreateDto2;
    private static UserUpdateDto userUpdateDto;

    @BeforeAll
    static void setup() {
        userCreateDto1 = UserCreateDto.builder()
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();
        userCreateDto2 = UserCreateDto.builder()
                .name("Billie Ryan")
                .email("Citlalli59@hotmail.com")
                .build();
        userUpdateDto = UserUpdateDto.builder()
                .name("Judith Hahn")
                .email("Ila_Friesen@hotmail.com")
                .build();
    }

    @Test
    void createUserTest() {
        UserDto userDto = userService.createUser(userCreateDto1);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userCreateDto1.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userCreateDto1.getName()));
        assertThat(user.getEmail(), equalTo(userCreateDto1.getEmail()));
        assertThat(userDto.getName(), equalTo(userCreateDto1.getName()));
        assertThat(userDto.getEmail(), equalTo(userCreateDto1.getEmail()));
    }

    @Test
    void findUsreByIdTest() {
        userService.createUser(userCreateDto2);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userCreateDto2.getEmail())
                .getSingleResult();

        UserDto userDto = userService.findUserById(user.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userCreateDto2.getName()));
        assertThat(user.getEmail(), equalTo(userCreateDto2.getEmail()));
        assertThat(userDto.getName(), equalTo(userCreateDto2.getName()));
        assertThat(userDto.getEmail(), equalTo(userCreateDto2.getEmail()));
    }

    @Test
    void findAllUsersTest() {
        List<UserCreateDto> sourceUsers = List.of(userCreateDto1, userCreateDto2);

        for (UserCreateDto userCreateDto : sourceUsers) {
            User entity = UserMapper.toUser(userCreateDto);
            em.persist(entity);
        }
        em.flush();

        // when
        Collection<UserDto> targetUsers = userService.findAllUsers();

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserCreateDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }

    }

    @Test
    void updateUserTest() {
        userService.createUser(userCreateDto1);
        TypedQuery<User> queryBefore = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userBefore = queryBefore.setParameter("email", userCreateDto1.getEmail())
                .getSingleResult();
        UserDto userDto = userService.updateUser(userBefore.getId(), userUpdateDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userUpdateDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userUpdateDto.getName()));
        assertThat(user.getEmail(), equalTo(userUpdateDto.getEmail()));
        assertThat(userDto.getName(), equalTo(userUpdateDto.getName()));
        assertThat(userDto.getEmail(), equalTo(userUpdateDto.getEmail()));
    }

    @Test
    void deleteUserTest() {
        userService.createUser(userCreateDto2);
        TypedQuery<User> queryBefore = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userBefore = queryBefore.setParameter("email", userCreateDto2.getEmail())
                .getSingleResult();

        UserDto userDto = userService.deleteUser(userBefore.getId());
        assertThat(userBefore.getId(), notNullValue());
        assertThat(userBefore.getName(), equalTo(userDto.getName()));
        assertThat(userBefore.getEmail(), equalTo(userDto.getEmail()));
    }


}
