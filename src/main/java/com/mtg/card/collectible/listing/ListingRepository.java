package com.mtg.card.collectible.listing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    Page<Listing> findByCollectible_Id(Long collectibleId, Pageable pageable);

}
