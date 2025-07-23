package ru.practicum.shareit.server.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.request.dto.AnswerDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    @Column(name = "requestor_id", nullable = false)
    private long requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();
    @Transient
    private List<AnswerDto> items = new ArrayList<>();

}
