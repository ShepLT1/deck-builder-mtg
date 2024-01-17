package com.mtg.card.collectible.listing;

import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards/listings")
public class ListingController {

    @Autowired
    ListingRepository listingRepository;

    @GetMapping("")
    Page<Listing> all(@RequestParam(required=false) Long collectibleId, Pageable pageable) {
        if (collectibleId != null) {
            Page<Listing> listingsByCollectible = listingRepository.findByCollectible_Id(collectibleId, pageable);
            if (!listingsByCollectible.hasContent()) {
                throw new EntityNotFoundException(collectibleId, "listing");
            }
            return listingsByCollectible;
        }
        return listingRepository.findAll(pageable);
    }

}
