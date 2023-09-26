package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select ir from ItemRequest as ir where not ir.requestor.id = ?1")
    List<ItemRequest> findEverythingWithoutRequestor(Long requestor, PageRequest pageRequest);

    List<ItemRequest> findAllByRequestor_Id(Long requestorId);
}
