package com.mtg.deck;

import com.mtg.admin.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    Page<Deck> findByNameIgnoreCaseAndUser(String name, User user, Pageable pageable);
    Page<Deck> findAllByUser(User user, Pageable pageable);
    Optional<Deck> findByIdAndUser(Long id, User user);

}
