package com.mtg.card.collectible;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectibleRepository extends JpaRepository<Collectible, Long> {

    Page<Collectible> findByCard_Id(Long cardId, Pageable pageable);

}
