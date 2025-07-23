package ru.practicum.shareit.server.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.server.item.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(long userId);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%',?1,'%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String searchQuery);

    List<Item> findByRequest_id(long requestId);

    List<Item> findByRequest_idIn(List<Long> itemRequestIdList);

}
