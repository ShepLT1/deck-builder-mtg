package com.mtg.card.base;

import com.mtg.deck.DeckService;
import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
class CardBaseController {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckService deckService;

    @GetMapping("")
    Page<Card> all(@RequestParam(required=false) String name, Pageable pageable) {
        if (name != null) {
            Page<Card> cardsByName = cardRepository.findByNameContainingIgnoreCase(name, pageable);
            if (!cardsByName.hasContent()) {
                throw new EntityNotFoundException(name, "card");
            }
            return cardsByName;
        }
        return cardRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    Card one(@PathVariable Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "card"));
    }

    @DeleteMapping("/{id}")
    void deleteCard(@PathVariable Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "card"));
        deckService.removeCardReferences(card);
        cardRepository.delete(card);
    }

}
