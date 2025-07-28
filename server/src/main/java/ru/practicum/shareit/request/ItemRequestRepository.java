package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByOrderByCreatedDesc();
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);
    List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long requesterId, Pageable pageable);
}
