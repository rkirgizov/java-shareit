package ru.practicum.shareit.user.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface UserRepository {
    List<User> findAll();

    User findById(Long id);

    User save(User user);

    void deleteById(Long userId);

    User findByEmail(String email);
}