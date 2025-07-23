package ru.practicum.shareit.server.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.server.request.Request;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @Column(name = "owner_id", nullable = false)
    private long owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

}