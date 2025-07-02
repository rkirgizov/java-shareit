package ru.practicum.shareit.item.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository {

    List<Item> findByUserId(Long userId);

    Item findByItemId(Long itemId);

    List<Item> findForNameOrDesc(String text);

    Item update(Long userId, Long itemId, Item item);

    Item save(Item item);

    void deleteByUserIdAndItemId(Long userId, Long itemId);
}