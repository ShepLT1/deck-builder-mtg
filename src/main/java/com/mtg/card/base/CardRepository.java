package com.mtg.card.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Card> findByIdIn(List<Long> idList);

}
