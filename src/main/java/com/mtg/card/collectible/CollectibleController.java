package com.mtg.card.collectible;

import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/collectibles")
public class CollectibleController {

    @Autowired
    CollectibleRepository collectibleRepository;

    @GetMapping("")
    Page<Collectible> all(@RequestParam(required=false) Long cardId, Pageable pageable) {
        if (cardId != null) {
            Page<Collectible> collectiblesByCardId = collectibleRepository.findByCard_Id(cardId, pageable);
            if (!collectiblesByCardId.hasContent()) {
                throw new EntityNotFoundException(cardId, "collectible card");
            }
            return collectiblesByCardId;
        }
        return collectibleRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    Collectible one(@PathVariable Long id) {
        return collectibleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "collectible card"));
    }

}
