package com.mtg.card.base;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByNameIgnoreCase(String name);

    List<Card> findByIdIn(List<Long> idList);

}
