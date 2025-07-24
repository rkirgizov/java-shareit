package ru.practicum.shareit.server.request.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.request.Request;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequestorOrderByCreatedAsc(long requestorId);

    List<Request> findByRequestorNotOrderByCreatedAsc(long requestorId);
}
