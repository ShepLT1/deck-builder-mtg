package com.mtg.deck;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    Page<Deck> findByNameIgnoreCase(String name, Pageable pageable);

}
