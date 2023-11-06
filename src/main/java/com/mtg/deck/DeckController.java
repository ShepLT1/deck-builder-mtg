package com.mtg.deck;

import com.mtg.card.base.CardRepository;
import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/decks")
class DeckController {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckService deckService;

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Page<Deck> all(@RequestParam(required=false) String name, Pageable pageable) {
        if (name != null) {
            Page<Deck> decksByName = deckRepository.findByNameIgnoreCase(name, pageable);
            if (!decksByName.hasContent()) {
                throw new EntityNotFoundException(name, "deck");
            }
            return decksByName;
        }
        return deckRepository.findAll(pageable);
    }
    // end::get-aggregate-root[]

    @PostMapping("")
    Deck newDeck(@RequestBody DeckDto preDeck) {
        Deck newDeck = new Deck(preDeck.getName(), preDeck.getColors());
        if (preDeck.getCardList() != null) {
            deckService.updateCardList(newDeck, preDeck.getCardList());
        }
        return deckRepository.save(newDeck);
    }

    @GetMapping("/{id}")
    Deck one(@PathVariable Long id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "deck"));
    }

    @PatchMapping("/{id}")
    Deck updateDeck(@RequestBody DeckDto partialDeck, @PathVariable Long id) {
        Deck deck = deckRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "deck"));
        deck.setName(partialDeck.getName());
        deckService.updateCardList(deck, partialDeck.getCardList());
        return deckRepository.save(deck);
    }

    @PatchMapping("/{deck_id}/cards/{card_id}")
    Deck addOrRemoveCard(@PathVariable Long deck_id, @PathVariable Long card_id, @RequestBody Map<String, String> fields) {
        return deckService.addOrRemoveCard(deck_id, card_id, fields);
    }

    @DeleteMapping("/{id}")
    void deleteDeck(@PathVariable Long id) {
        deckRepository.delete(deckRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "deck")));
    }

}
