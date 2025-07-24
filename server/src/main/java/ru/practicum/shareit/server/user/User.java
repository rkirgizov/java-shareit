package ru.practicum.shareit.server.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "email", length = 255, nullable = false)
    private String email;
}

